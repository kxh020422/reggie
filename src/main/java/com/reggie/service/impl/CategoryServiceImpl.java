package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.pojo.Category;
import com.reggie.pojo.Dish;
import com.reggie.pojo.Setmeal;
import com.reggie.service.CategoryService;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author co
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2022-10-07 14:58:13
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        long countDish = dishService.count(dishQueryWrapper);
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (countDish > 0) {
            //已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类下已关联菜品，不能删除");
        }


        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        long countSetmeal = setmealService.count(setmealQueryWrapper);
        if (countSetmeal > 0) {
            //已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类下已关联套餐，不能删除");
        }


        //正常删除分类
        super.removeById(id);

    }
}




