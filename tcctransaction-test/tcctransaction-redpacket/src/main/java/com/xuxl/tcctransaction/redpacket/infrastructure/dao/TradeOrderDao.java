package com.xuxl.tcctransaction.redpacket.infrastructure.dao;

import com.xuxl.tcctransaction.redpacket.domain.entity.TradeOrder;

public interface TradeOrderDao {

    void insert(TradeOrder tradeOrder);

    void update(TradeOrder tradeOrder);

    TradeOrder findByMerchantOrderNo(String merchantOrderNo);
}
