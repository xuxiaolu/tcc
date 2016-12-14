package com.xuxl.tcctransaction.redpacket.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.xuxl.tcctransaction.redpacket.domain.entity.TradeOrder;
import com.xuxl.tcctransaction.redpacket.infrastructure.dao.TradeOrderDao;

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
