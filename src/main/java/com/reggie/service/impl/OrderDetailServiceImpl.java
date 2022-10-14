package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.pojo.OrderDetail;
import com.reggie.service.OrderDetailService;
import com.reggie.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author co
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2022-10-08 16:12:26
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




