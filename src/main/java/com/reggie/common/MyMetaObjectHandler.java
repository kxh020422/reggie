package com.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName MyMetaObjectHandler
 * @Date 2022/10/7 8:26
 * 元数据对象处理器 用来自动填充数据库表公共字段
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    public static final Logger logger = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    /**
     * @date: 2022/10/7 8:38
     * @remark: 插入操作自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        logger.info("公共字段自动填充[INSERT]...");
        logger.info(metaObject.toString());

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getId());
        metaObject.setValue("updateUser", BaseContext.getId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        logger.info("公共字段自动填充[UPDATE]...");
        logger.info(metaObject.toString());

//        long id = Thread.currentThread().getId();
//        String name = Thread.currentThread().getName();
//        logger.info("当前线程id为{}，线程名称为{}", id, name);


        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getId());
    }
}
