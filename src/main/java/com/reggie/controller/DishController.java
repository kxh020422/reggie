package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.pojo.Category;
import com.reggie.pojo.Dish;
import com.reggie.pojo.DishFlavor;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import net.sf.jsqlparser.util.deparser.CreateTableDeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName DishController
 * @Date 2022/10/8 10:43
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    public static final Logger logger = LoggerFactory.getLogger(DishController.class);

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        logger.info("新增的菜品为：{}", dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByDesc(Dish::getUpdateTime);
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.eq(Dish::getIsDeleted, 0);

        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//根据id显示分类对象
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * @date: 2022/10/8 19:31
     * @remark: 根据id查询对应的菜品信息以及对应的口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        logger.info("新增的菜品为：{}", dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @PostMapping("/status/{status}")
    public R<String> handleDishStatus(@PathVariable Integer status,
                                      @RequestParam("id") List<Long> ids) {
        if (ids.size() < 2) {
            Long id = ids.get(0);
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
            return R.success("状态修改成功");
        } else {
            List<Dish> dishList = null;
            dishList = ids.stream().map((item) -> {
                Dish dish = dishService.getById(item);
                dish.setStatus(status);
                return dish;
            }).collect(Collectors.toList());
            dishService.updateBatchById(dishList);
            return R.success("批量修改成功");
        }
    }

    @DeleteMapping
    public R<String> delete(@RequestParam("id") List<Long> ids) {
        if (ids.size() < 2) {
            Long id = ids.get(0);
            logicDelete(id);
            return R.success("删除成功");
        } else {
//            ids.stream().map((id) -> {
//                logicDelete(id);
//                return id;
//            }).toList();
            ids.stream().peek(this::logicDelete).toList();

            return R.success("批量删除成功");
        }
    }


    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList != null) {
            //如果存在 直接返回 无需查询数据库
            return R.success(dishDtoList);
        }
        Long categoryId = dish.getCategoryId();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        dishDtoList = dishList.stream().map(dishItem -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dishItem, dishDto);
            Long dishId = dishItem.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).toList();

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到redis
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }


    public void logicDelete(Long id) {
        Dish dish = dishService.getById(id);
        dish.setIsDeleted(1);
        dishService.updateById(dish);
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorQueryWrapper)
                .stream().map((item) -> {
                    item.setIsDeleted(1);
                    return item;
                }).toList();
        dishFlavorService.updateBatchById(dishFlavorList);
    }

}
