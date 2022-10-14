package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.pojo.Employee;
import com.reggie.service.EmployeeService;
import com.reggie.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author co
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2022-10-05 11:35:36
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{


}




