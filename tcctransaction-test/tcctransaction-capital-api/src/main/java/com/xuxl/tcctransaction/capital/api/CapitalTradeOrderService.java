package com.xuxl.tcctransaction.capital.api;

import com.xuxl.tcctransaction.api.TransactionContext;
import com.xuxl.tcctransaction.capital.api.dto.CapitalTradeOrderDto;

public interface CapitalTradeOrderService {

    public String record(TransactionContext transactionContext,CapitalTradeOrderDto tradeOrderDto);

}
