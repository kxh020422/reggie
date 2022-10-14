package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.pojo.AddressBook;
import com.reggie.service.AddressBookService;
import com.reggie.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author co
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-10-09 20:18:04
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




