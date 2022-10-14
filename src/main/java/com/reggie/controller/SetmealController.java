package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.dto.OrdersDto;
import com.reggie.dto.SetmealDto;
import com.reggie.pojo.Category;
import com.reggie.pojo.Dish;
import com.reggie.pojo.Setmeal;
import com.reggie.pojo.SetmealDish;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SetmealController
 * @Date 2022/10/9 13:11
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    public static final Logger logger = LoggerFactory.getLogger(SetmealController.class);

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * @date: 2022/10/9 13:34
     * @remark: 新增套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {

        logger.info("套餐信息：{}", setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐信息成功");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.eq(Setmeal::getIsDeleted, 0);
        //根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        List<SetmealDto> setmealDtoList = null;
        setmealDtoList = pageInfo.getRecords().stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).toList();
        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    @GetMapping("/{setmealId}")
    public R<SetmealDto> getById(@PathVariable Long setmealId) {

        SetmealDto setmealDish = setmealService.getWithSetmealDish(setmealId);

        return R.success(setmealDish);
    }


    @PutMapping
    public R<String> updateWithSetmealDish(@RequestBody SetmealDto setmealDto) {

        setmealService.updateWithSetmealDish(setmealDto);

        return R.success("修改成功");
    }

    /**
     * @date: 2022/10/14 20:33
     * @remark: @CacheEvict(value = "setmealCache", allEntries = true) 将"setmealCache"下的所有缓存数据 全部删除
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam("id") List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }



    @PostMapping("/status/{status}")
    public R<String> handleStatus(@PathVariable Integer status,
                                  @RequestParam("id") List<Long> ids) {
        setmealService.handleStatus(status, ids);
        return R.success("修改状态成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache",
            key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    @GetMapping("/dish/{setmealId}")
    public R<List<DishDto>> getByDishId(@PathVariable Long setmealId){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        queryWrapper.orderByDesc(SetmealDish::getUpdateTime);
//        SetmealDto withSetmealDish = setmealService.getWithSetmealDish(setmealId);

        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);

        List<DishDto> list = setmealDishList.stream().map(setmealDish -> {
            Long dishId = setmealDish.getDishId();
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(setmealDish, dishDto);
            Dish dish = dishService.getById(dishId);
            BeanUtils.copyProperties(dish, dishDto);
            return dishDto;
        }).toList();
        return R.success(list);
    }


}
