package com.xuxl.tcctransaction.order.infrastructure.dao;

import com.xuxl.tcctransaction.order.domain.entity.Order;

public interface OrderDao {

    public void insert(Order order);

    public void update(Order order);

    Order findByMerchantOrderNo(String merchantOrderNo);
}
