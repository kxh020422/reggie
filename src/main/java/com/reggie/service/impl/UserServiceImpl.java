package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.pojo.User;
import com.reggie.service.UserService;
import com.reggie.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author co
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2022-10-17 10:35:23
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




