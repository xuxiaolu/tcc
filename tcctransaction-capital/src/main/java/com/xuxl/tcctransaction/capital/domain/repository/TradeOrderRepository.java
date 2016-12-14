package com.xuxl.tcctransaction.capital.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.xuxl.tcctransaction.capital.domain.entity.TradeOrder;
import com.xuxl.tcctransaction.capital.infrastructure.dao.TradeOrderDao;

/**
 * Created by twinkle.zhou on 16/11/14.
 */
@Repository
public class TradeOrderRepository {

    @Autowired
    TradeOrderDao tradeOrderDao;

    public void insert(TradeOrder tradeOrder){
        tradeOrderDao.insert(tradeOrder);
    }

    public void update(TradeOrder tradeOrder){
        tradeOrderDao.update(tradeOrder);
    }

    public TradeOrder findByMerchantOrderNo(String merchantOrderNo){
        return tradeOrderDao.findByMerchantOrderNo(merchantOrderNo);
    }

}
