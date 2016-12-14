package com.xuxl.tcctransaction.capital.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.xuxl.tcctransaction.Compensable;
import com.xuxl.tcctransaction.api.TransactionContext;
import com.xuxl.tcctransaction.capital.api.CapitalTradeOrderService;
import com.xuxl.tcctransaction.capital.api.dto.CapitalTradeOrderDto;
import com.xuxl.tcctransaction.capital.domain.entity.CapitalAccount;
import com.xuxl.tcctransaction.capital.domain.entity.TradeOrder;
import com.xuxl.tcctransaction.capital.domain.repository.CapitalAccountRepository;
import com.xuxl.tcctransaction.capital.domain.repository.TradeOrderRepository;
import com.xuxl.tcctransaction.dubbo.config.DubboService;

@Component("capitalTradeOrderService")
@DubboService(retries = 0,timeout = 6000,interfaceClass = CapitalTradeOrderService.class)
public class CapitalTradeOrderServiceImpl implements CapitalTradeOrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(CapitalTradeOrderServiceImpl.class);

    @Autowired
    CapitalAccountRepository capitalAccountRepository;

    @Autowired
    TradeOrderRepository tradeOrderRepository;
    
    @Autowired
    TransactionTemplate transactionTemplate;

    @Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord")
    public String record(TransactionContext transactionContext, final CapitalTradeOrderDto tradeOrderDto) {
    	logger.info("========================capital try record called============================");
        final TradeOrder tradeOrder = new TradeOrder(
                tradeOrderDto.getSelfUserId(),
                tradeOrderDto.getOppositeUserId(),
                tradeOrderDto.getMerchantOrderNo(),
                tradeOrderDto.getAmount()
        );
        transactionTemplate.execute(new TransactionCallback<Boolean>() {
			public Boolean doInTransaction(TransactionStatus status) {
				try {
					tradeOrderRepository.insert(tradeOrder);
					CapitalAccount transferFromAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());
					transferFromAccount.transferFrom(tradeOrderDto.getAmount());
					capitalAccountRepository.save(transferFromAccount);
					return true;
				} catch(Exception e) {
					status.isRollbackOnly();
					return false;
				}
			}
		});
        return "success";
    }
    

    @Transactional
    public void confirmRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
    	logger.info("====================capital confirm record called====================");
        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        if(null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
        	tradeOrder.confirm();
        	tradeOrderRepository.update(tradeOrder);
        	CapitalAccount transferToAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());
        	transferToAccount.transferTo(tradeOrderDto.getAmount());
        	capitalAccountRepository.save(transferToAccount);
        }
    }

    @Transactional
    public void cancelRecord(TransactionContext transactionContext, CapitalTradeOrderDto tradeOrderDto) {
    	logger.info("=====================capital cancel record called=============================");
        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        if(null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.cancel();
            tradeOrderRepository.update(tradeOrder);
            CapitalAccount capitalAccount = capitalAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());
            capitalAccount.cancelTransfer(tradeOrderDto.getAmount());
            capitalAccountRepository.save(capitalAccount);
        }
    }
}
