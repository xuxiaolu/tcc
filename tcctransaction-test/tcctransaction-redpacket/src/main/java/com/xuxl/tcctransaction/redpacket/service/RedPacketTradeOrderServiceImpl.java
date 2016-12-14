package com.xuxl.tcctransaction.redpacket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.xuxl.tcctransaction.Compensable;
import com.xuxl.tcctransaction.api.TransactionContext;
import com.xuxl.tcctransaction.dubbo.config.DubboService;
import com.xuxl.tcctransaction.redpacket.api.RedPacketTradeOrderService;
import com.xuxl.tcctransaction.redpacket.api.dto.RedPacketTradeOrderDto;
import com.xuxl.tcctransaction.redpacket.domain.entity.RedPacketAccount;
import com.xuxl.tcctransaction.redpacket.domain.entity.TradeOrder;
import com.xuxl.tcctransaction.redpacket.domain.repository.RedPacketAccountRepository;
import com.xuxl.tcctransaction.redpacket.domain.repository.TradeOrderRepository;

@Component("redPacketTradeOrderService")
@DubboService(retries = 0,timeout = 6000,interfaceClass = RedPacketTradeOrderService.class)
public class RedPacketTradeOrderServiceImpl implements RedPacketTradeOrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(RedPacketTradeOrderServiceImpl.class);

    @Autowired
    RedPacketAccountRepository redPacketAccountRepository;

    @Autowired
    TradeOrderRepository tradeOrderRepository;

    @Compensable(confirmMethod = "confirmRecord",cancelMethod = "cancelRecord")
    @Transactional
    public String record(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
    	logger.info("======================red packet try record called=============================");
        TradeOrder tradeOrder = new TradeOrder(
                tradeOrderDto.getSelfUserId(),
                tradeOrderDto.getOppositeUserId(),
                tradeOrderDto.getMerchantOrderNo(),
                tradeOrderDto.getAmount()
        );
        tradeOrderRepository.insert(tradeOrder);
        RedPacketAccount transferFromAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());
        transferFromAccount.transferFrom(tradeOrderDto.getAmount());
        redPacketAccountRepository.save(transferFromAccount);
        return "success";
    }

    @Transactional
    public void confirmRecord(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
    	logger.info("======================red packet confirm record called=============================");
        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        if(null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
        	tradeOrder.confirm();
        	tradeOrderRepository.update(tradeOrder);
        	RedPacketAccount transferToAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());
        	transferToAccount.transferTo(tradeOrderDto.getAmount());
        	redPacketAccountRepository.save(transferToAccount);
        }
    }

    @Transactional
    public void cancelRecord(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
    	logger.info("======================red packet cancel record called=============================");
        TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());
        if(null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
            tradeOrder.cancel();
            tradeOrderRepository.update(tradeOrder);
            RedPacketAccount capitalAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());
            capitalAccount.cancelTransfer(tradeOrderDto.getAmount());
            redPacketAccountRepository.save(capitalAccount);
        }
    }
}
