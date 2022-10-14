package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomException;
import com.reggie.common.R;
import com.reggie.pojo.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName ShoppingCartController
 * @Date 2022/10/9 23:38
 */

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

    @Autowired
    private ShoppingCartService shoppingCartService;


    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        logger.info("购物车数据：{}", shoppingCart);

        //设置用户id 指定当前时哪个用户的购物车数据
        Long currentUserId = BaseContext.getId();
        shoppingCart.setUserId(currentUserId);
        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentUserId);
        if (dishId != null) {
            //添加到购物车的是单个菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            //添加到购物车的是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, setmealId);

        }
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if (shoppingCartServiceOne != null) {
            //如果已经存在 那就在原有的数量基础上加一
            Integer number = shoppingCartServiceOne.getNumber();
            shoppingCartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        } else {
            //如果不存在 则添加到购物车
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne = shoppingCart;
        }
        return R.success(shoppingCartServiceOne);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long currentUserId = BaseContext.getId();
        shoppingCart.setUserId(currentUserId);
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentUserId);
        if (dishId != null) {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if (shoppingCartServiceOne != null) {
            Integer number = shoppingCartServiceOne.getNumber();
            if(number <= 0) {
                shoppingCartService.removeById(shoppingCartServiceOne.getId());
                return R.error("数量已清空");
            }
            shoppingCartServiceOne.setNumber(number - 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        }

        shoppingCartServiceOne.setCreateTime(LocalDateTime.now());
        return R.success(shoppingCartServiceOne);

    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getId());
        shoppingCartQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(shoppingCartQueryWrapper);

        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getId());
        shoppingCartService.remove(shoppingCartQueryWrapper);
        return R.success("清空成功");
    }
}
