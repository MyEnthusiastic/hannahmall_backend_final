package com.hannah.hannahmall.product.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hannah.hannahmall.common.base.Entity;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class MyMetaObjectHandlerConfiguration implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName(Entity.CREATE_TIME, LocalDateTime.now(), metaObject);
        this.setFieldValByName(Entity.UPDATE_TIME, LocalDateTime.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName(Entity.UPDATE_TIME, LocalDateTime.now(), metaObject);

    }
}
