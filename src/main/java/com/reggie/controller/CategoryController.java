package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.pojo.Category;
import com.reggie.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CategoryController
 * @Date 2022/10/7 14:59
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    public static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    /**
     * @date: 2022/10/7 15:27
     * @remark: 新增分类
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * @date: 2022/10/7 15:45
     * @remark: 分页查询
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }


    @DeleteMapping
    public R<String> delete(Long id) {
        logger.info("删除分类，id为：{}", id);
        categoryService.remove(id);
        return R.success("删除分类信息成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * @date: 2022/10/8 11:02
     * @remark: 根据条件查询分类数据
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> categoryList = categoryService.list(queryWrapper);

        return R.success(categoryList);
    }




}
