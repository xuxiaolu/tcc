package com.xuxl.tcctransaction.order.infrastructure.dao;

import com.xuxl.tcctransaction.order.domain.entity.OrderLine;

public interface OrderLineDao {
    void insert(OrderLine orderLine);
}
