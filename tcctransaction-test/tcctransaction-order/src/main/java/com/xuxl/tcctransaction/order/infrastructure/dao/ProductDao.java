package com.xuxl.tcctransaction.order.infrastructure.dao;


import java.util.List;

import com.xuxl.tcctransaction.order.domain.entity.Product;

public interface ProductDao {

    Product findById(long productId);

    List<Product> findByShopId(long shopId);
}
