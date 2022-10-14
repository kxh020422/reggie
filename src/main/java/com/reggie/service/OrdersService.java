package com.reggie.service;

import com.reggie.pojo.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author co
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2022-10-08 16:11:15
*/
public interface OrdersService extends IService<Orders> {
    /**
     * @date: 2022/10/10 10:47
     * @remark: 用户下单
     */
    void submit(Orders orders);


    /**
     * @date: 2022/10/10 21:16
     * @remark: 再来一单
     */
    void again(Orders orders);
}
