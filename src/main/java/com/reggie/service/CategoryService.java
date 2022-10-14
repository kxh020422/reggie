package com.reggie.service;

import com.reggie.pojo.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;

/**
* @author co
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2022-10-07 14:58:13
*/
public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
