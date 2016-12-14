package com.xuxl.tcctransaction.redpacket.api;

import com.xuxl.tcctransaction.api.TransactionContext;
import com.xuxl.tcctransaction.redpacket.api.dto.RedPacketTradeOrderDto;

public interface RedPacketTradeOrderService {

    public String record(TransactionContext transactionContext,RedPacketTradeOrderDto tradeOrderDto);
}
