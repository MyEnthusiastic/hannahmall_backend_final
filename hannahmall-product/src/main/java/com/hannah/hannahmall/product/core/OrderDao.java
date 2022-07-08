package com.hannah.hannahmall.product.core;

import com.hannah.hannahmall.product.core.entity.OrderEntity;

import java.util.List;

public interface OrderDao {



    public List<OrderEntity> selectAll();



    public void update();

}
