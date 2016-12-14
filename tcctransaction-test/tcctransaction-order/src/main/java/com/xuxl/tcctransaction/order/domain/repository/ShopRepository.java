package com.xuxl.tcctransaction.order.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.xuxl.tcctransaction.order.domain.entity.Shop;
import com.xuxl.tcctransaction.order.infrastructure.dao.ShopDao;

@Repository
public class ShopRepository {

    @Autowired
    ShopDao shopDao;

    public Shop findById(long id) {

        return shopDao.findById(id);
    }
}
