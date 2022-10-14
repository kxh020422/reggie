package com.reggie.service;

import com.reggie.dto.DishDto;
import com.reggie.pojo.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author co
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2022-10-07 16:15:24
*/
public interface DishService extends IService<Dish> {

    //新增菜品 同时插入菜品对应的口味数据 需要同时操作两张表：dish dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息以及对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);


}
