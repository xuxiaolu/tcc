package com.xuxl.tcctransaction.order.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.xuxl.tcctransaction.order.domain.entity.Order;
import com.xuxl.tcctransaction.order.domain.entity.OrderLine;
import com.xuxl.tcctransaction.order.infrastructure.dao.OrderDao;
import com.xuxl.tcctransaction.order.infrastructure.dao.OrderLineDao;

/**
 * Created by changming.xie on 4/1/16.
 */
@Repository
public class OrderRepository {

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderLineDao orderLineDao;

    public void createOrder(Order order) {
        orderDao.insert(order);

        for(OrderLine orderLine:order.getOrderLines()) {
            orderLineDao.insert(orderLine);
        }
    }

    public void updateOrder(Order order) {
        orderDao.update(order);
    }

    public Order findByMerchantOrderNo(String merchantOrderNo){
        return orderDao.findByMerchantOrderNo(merchantOrderNo);
    }
}
