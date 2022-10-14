package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomException;
import com.reggie.pojo.*;
import com.reggie.service.*;
import com.reggie.mapper.OrdersMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
* @author co
* @description 针对表【orders(订单表)】的数据库操作Service实现
* @createDate 2022-10-08 16:11:15
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService{

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    /**
     * @date: 2022/10/10 10:47
     * @remark: 用户下单
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户id
        Long currentUserID = BaseContext.getId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentUserID);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(currentUserID);

        //查询用户地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址信息有误，不能下单");
        }


        AtomicInteger amount = new AtomicInteger(0);


        long orderId = IdWorker.getId();
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).toList();

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentUserID);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据
        orderDetailService.saveBatch(orderDetailList);

        //清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

    }

    @Override
    @Transactional
    public void again(Orders orders) {
        Long ordersId = orders.getId();
        LambdaQueryWrapper<Orders> ordersQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orders = this.getById(ordersId);
        Orders newOrders = new Orders();
        BeanUtils.copyProperties(orders, newOrders, "id","number", "orderTime", "checkoutTime", "status");
        long newOrderId = IdWorker.getId();
        newOrders.setNumber(String.valueOf(newOrderId));
        newOrders.setId(newOrderId);
        newOrders.setOrderTime(LocalDateTime.now());
        newOrders.setCheckoutTime(LocalDateTime.now());
        newOrders.setStatus(2);

        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, ordersId);
        List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);
        orderDetailList = orderDetailList.stream().map(orderDetail -> {
            orderDetail.setId(null);
            orderDetail.setOrderId(newOrderId);
            return orderDetail;
        }).toList();

        this.save(newOrders);
        orderDetailService.saveBatch(orderDetailList);
    }
}




