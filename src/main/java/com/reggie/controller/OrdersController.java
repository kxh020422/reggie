package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.dto.OrdersDto;
import com.reggie.pojo.OrderDetail;
import com.reggie.pojo.Orders;
import com.reggie.service.OrderDetailService;
import com.reggie.service.OrdersService;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.lang.management.LockInfo;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @ClassName OrderController
 * @Date 2022/10/9 16:12
 */
@RestController
@RequestMapping("/order")
public class OrdersController {

    public static final Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/page")
    public R<Page<Orders>> page(int page,
                                int pageSize,
                                @RequestParam(required = false) Integer number,
                                @RequestParam(required = false, name = "beginTime") String beginTimeStr,
                                @RequestParam(required = false, name = "endTime") String endTimeStr) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersQueryWrapper = new LambdaQueryWrapper<>();
        ordersQueryWrapper.gt(StringUtils.isNotEmpty(beginTimeStr), Orders::getOrderTime, beginTimeStr)
                .lt(StringUtils.isNotEmpty(endTimeStr), Orders::getOrderTime, endTimeStr)
                .like(number != null, Orders::getNumber, number);
        ordersService.page(pageInfo, ordersQueryWrapper);
        return R.success(pageInfo);
    }


    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        logger.info("订单数据：{}", orders.toString());
        ordersService.submit(orders);
        return null;
    }


    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(int page, int pageSize) {

        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        Long userId = BaseContext.getId();
        LambdaQueryWrapper<Orders> ordersQueryWrapper = new LambdaQueryWrapper<>();
        ordersQueryWrapper.eq(Orders::getUserId, userId);
        ordersService.page(ordersPage, ordersQueryWrapper);
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");
        List<OrdersDto> ordersDtoList = ordersPage.getRecords().stream().map(orders -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);
            Long ordersId = orders.getId();
            LambdaQueryWrapper<OrderDetail> orderDetailQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailQueryWrapper.eq(OrderDetail::getOrderId, ordersId);
            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailQueryWrapper);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).toList();
        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return R.success("更新成功");
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders) {
        ordersService.again(orders);
        return R.success("再来一单成功！");
    }

}
