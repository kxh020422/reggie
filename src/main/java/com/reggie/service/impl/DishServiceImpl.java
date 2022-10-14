package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.dto.DishDto;
import com.reggie.pojo.Dish;
import com.reggie.pojo.DishFlavor;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import com.reggie.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author co
 * @description 针对表【dish(菜品管理)】的数据库操作Service实现
 * @createDate 2022-10-07 16:15:24
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * @date: 2022/10/8 16:49
     * @remark: 新增菜品的同时 保存对应的口味数据
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到dish表
        this.save(dishDto);
        Long dishId = dishDto.getId();

        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
//        for (DishFlavor dishFlavor : dishFlavorList) {
//            dishFlavor.setDishId(dishId);
//        }
        dishFlavorList = dishFlavorList.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到dish_flavor
        dishFlavorService.saveBatch(dishFlavorList);

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor查询
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(dish != null, DishFlavor::getDishId, dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorQueryWrapper);
        dishDto.setFlavors(dishFlavorList);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理更新前菜品对应的口味数据
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(dishFlavorQueryWrapper);
        //添加更新后的菜品对应的口味数据
        List<DishFlavor> flavorList = dishDto.getFlavors();
        flavorList = flavorList.stream().map((item -> {
            item.setDishId(dishDto.getId());
            return item;
        })).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavorList);
    }
}




