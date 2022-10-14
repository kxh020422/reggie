package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.pojo.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import com.reggie.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

/**
* @author co
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2022-10-09 23:37:08
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{

}




