package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.pojo.Employee;
import com.reggie.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @ClassName EmployeeController
 * @Date 2022/10/5 11:41
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    public static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1.将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.判断数据是否查询到
        if (emp == null) {
            return R.error("登陆失败");
        }

        //4.密码比对
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //5.查看员工状态是否可用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6.登录成功将用户Id存入session并返回登录成功结果
        HttpSession session = request.getSession();
        session.setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * @date: 2022/10/5 22:15
     * @remark: 员工退出
     */
    @PostMapping("/logout")
    public R<String> logOut(HttpServletRequest request) {
        //清理session中保存的当前登录的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * @date: 2022/10/6 15:04
     * @remark: 新增员工
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
        logger.info("员工信息：" + employee.toString());

        //设置初始密码为123456，对其进行md5加密处理
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增成功");
    }

    /**
     * @date: 2022/10/6 16:29
     * @remark: 员工信息分页查询
     */
    @GetMapping("/page")
    @ResponseBody
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        logger.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * @date: 2022/10/6 21:17
     * @remark: 根据id修改员工信息
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        logger.info(employee.toString());

//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        logger.info("--------------EmployeeController_update--------------");
        long id = Thread.currentThread().getId();
        String name = Thread.currentThread().getName();
        logger.info("当前线程id为{}，线程名称为{}", id, name);
        logger.info("--------------EmployeeController_update--------------");

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }


    @GetMapping("/{id}")
    public R<Employee> getEmpById(@PathVariable String id) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getId, id);
        Employee employee = employeeService.getOne(queryWrapper);
        if (employee != null) {
            return R.success(employee);
        } else {
            return R.error("没有查询到员工信息");
        }
    }
}
