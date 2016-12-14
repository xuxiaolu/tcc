package com.xuxl.tcctransaction.order.infrastructure.dao;

import com.xuxl.tcctransaction.order.domain.entity.Shop;

public interface ShopDao {
	
    Shop findById(long id);
}
