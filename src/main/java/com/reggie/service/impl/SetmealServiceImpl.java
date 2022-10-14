package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.dto.SetmealDto;
import com.reggie.pojo.Category;
import com.reggie.pojo.Setmeal;
import com.reggie.pojo.SetmealDish;
import com.reggie.service.CategoryService;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import com.reggie.mapper.SetmealMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author co
 * @description 针对表【setmeal(套餐)】的数据库操作Service实现
 * @createDate 2022-10-07 16:15:34
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    private static final Logger logger = LoggerFactory.getLogger(SetmealServiceImpl.class);

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作表setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        setmealDishList = setmealDishList.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).toList();

        //保存套餐以及菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishList);

    }

    @Override
    public SetmealDto getWithSetmealDish(Long setmealId) {
        Setmeal setmeal = this.getById(setmealId);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.eq(setmeal != null, SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishList = setmealDishService.list(setmealDishQueryWrapper);
        setmealDto.setSetmealDishes(setmealDishList);

        LambdaQueryWrapper<Category> categoryQueryWrapper = new LambdaQueryWrapper<>();
        categoryQueryWrapper.eq(setmeal != null, Category::getId, setmeal.getCategoryId());
        String categoryName = categoryService.getOne(categoryQueryWrapper).getName();

        setmealDto.setCategoryName(categoryName);

        return setmealDto;
    }

    @Override
    public void updateWithSetmealDish(SetmealDto setmealDto) {
        logger.info(setmealDto.toString());
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishQueryWrapper);

        List<SetmealDish> setmealDishList = null;
        setmealDishList = setmealDto.getSetmealDishes().stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).toList();

        setmealDishService.saveBatch(setmealDishList);

    }

    @Override
    public void removeWithDish(List<Long> ids) {

        //查询套餐状态确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        List<Setmeal> setmealList = this.list(queryWrapper);

        //如果不能删除，抛出一个异常
        if (setmealList.size() > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        queryWrapper.clear();
        queryWrapper.in(Setmeal::getId, ids);

        //如果可以删除，先删除套餐中的数据（逻辑删除）
        setmealList = this.list(queryWrapper)
                .stream().map(setmeal -> {
                    setmeal.setIsDeleted(1);
                    return setmeal;
                }).collect(Collectors.toList());

        this.updateBatchById(setmealList);

        //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.in(SetmealDish::getSetmealId, ids);

        List<SetmealDish> setmealDishList;
        setmealDishList = setmealDishService.list(setmealDishQueryWrapper)
                .stream().map(setmealDish -> {
                    setmealDish.setIsDeleted(1);
                    return setmealDish;
                }).toList();

        setmealDishService.updateBatchById(setmealDishList);
    }

    @Override
    public void handleStatus(Integer status, List<Long> ids) {

        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();

        setmealQueryWrapper.in(Setmeal::getId, ids);

        List<Setmeal> setmealList = this.list(setmealQueryWrapper).stream().map(setmeal -> {
            setmeal.setStatus(status);
            return setmeal;
        }).toList();

        this.updateBatchById(setmealList);


    }
}




