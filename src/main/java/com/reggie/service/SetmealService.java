package com.reggie.service;

import com.reggie.dto.SetmealDto;
import com.reggie.pojo.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author co
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2022-10-07 16:15:34
*/
public interface SetmealService extends IService<Setmeal> {

    /**
     * @date: 2022/10/9 13:45
     * @remark: 新增套餐以及套餐与菜品之间的关联关系
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * @date: 2022/10/9 14:47
     * @remark: 获取套餐基本信息 以及套餐包含菜品信息
     */
    SetmealDto getWithSetmealDish(Long setmealId);


    /**
     * @date: 2022/10/9 15:00
     * @remark: 获取套餐基本信息 以及套餐包含菜品信息
     */
    void updateWithSetmealDish(SetmealDto setmealDto);


    void removeWithDish(List<Long> ids);


    void handleStatus(Integer status, List<Long> ids);
}
