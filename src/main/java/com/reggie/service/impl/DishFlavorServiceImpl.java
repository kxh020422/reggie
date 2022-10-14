package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.pojo.DishFlavor;
import com.reggie.service.DishFlavorService;
import com.reggie.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author co
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2022-10-08 10:41:13
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




