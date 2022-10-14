package com.reggie.dto;


import com.reggie.pojo.OrderDetail;
import com.reggie.pojo.Orders;
import java.util.List;


public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

    public OrdersDto() {
    }

    public OrdersDto(String userName, String phone, String address, String consignee, List<OrderDetail> orderDetails) {
        this.userName = userName;
        this.phone = phone;
        this.address = address;
        this.consignee = consignee;
        this.orderDetails = orderDetails;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getConsignee() {
        return consignee;
    }

    @Override
    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
