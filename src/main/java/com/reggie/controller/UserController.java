package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.reggie.common.R;
import com.reggie.pojo.User;
import com.reggie.service.UserService;
import com.reggie.utils.SMSUtils;
import com.reggie.utils.ValidateCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.pattern.PathPattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.ImageProducer;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName UserController
 * @Date 2022/10/9 18:34
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {

            //随机生成一个验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//            String code = "1234";
            logger.info("======================={}=======================", code);

            //调用阿里云提供的短信服务API完成发送短信
            SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", phone, code);

            //将生成的验证码保存到session
//            session.setAttribute(phone, code);

            //将生成的验证码存储到redis中
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> userMap, HttpSession session) {
        logger.info(userMap.toString());

        // 获取手机号
        String phone = userMap.get("phone");

        // 获取验证码
        String code = userMap.get("code");

        // 从session中获取验证码
//        Object codeInSession = session.getAttribute(phone);


        //从redis中获取短信验证码
        Object codeInRedis = redisTemplate.opsForValue().get(phone);


        // 验证码比对
        if (codeInRedis != null && codeInRedis.equals(code)) {
            // 如果能够比对成功，则说明登陆成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //判断当前手机号对一个的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            //如果用户登录成功 删除redis中的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功！");
    }


}
