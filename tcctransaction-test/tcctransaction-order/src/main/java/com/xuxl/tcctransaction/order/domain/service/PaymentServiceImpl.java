package com.xuxl.tcctransaction.order.domain.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.xuxl.tcctransaction.Compensable;
import com.xuxl.tcctransaction.capital.api.CapitalTradeOrderService;
import com.xuxl.tcctransaction.capital.api.dto.CapitalTradeOrderDto;
import com.xuxl.tcctransaction.dubbo.config.DubboReference;
import com.xuxl.tcctransaction.order.domain.entity.Order;
import com.xuxl.tcctransaction.order.domain.repository.OrderRepository;
import com.xuxl.tcctransaction.redpacket.api.RedPacketTradeOrderService;
import com.xuxl.tcctransaction.redpacket.api.dto.RedPacketTradeOrderDto;

@Service
public class PaymentServiceImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    OrderRepository orderRepository;
    
    @Autowired
    TransactionTemplate transactionTemplate;
    
    @DubboReference
    CapitalTradeOrderService capitalTradeOrderService;

    @DubboReference
    RedPacketTradeOrderService redPacketTradeOrderService;

    @Compensable(confirmMethod = "confirmMakePayment", cancelMethod = "cancelMakePayment")
    public void makePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {
    	logger.info("====================order try make payment called===============================");
        transactionTemplate.execute(new TransactionCallback<Order>() {
			public Order doInTransaction(TransactionStatus status) {
				order.pay(redPacketPayAmount, capitalPayAmount);
		        orderRepository.updateOrder(order);
				return order;
			}
		});
        capitalTradeOrderService.record(null, buildCapitalTradeOrderDto(order));
        redPacketTradeOrderService.record(null, buildRedPacketTradeOrderDto(order));
    }
    
    private CapitalTradeOrderDto buildCapitalTradeOrderDto(Order order) {
        CapitalTradeOrderDto tradeOrderDto = new CapitalTradeOrderDto();
        tradeOrderDto.setAmount(order.getCapitalPayAmount());
        tradeOrderDto.setMerchantOrderNo(order.getMerchantOrderNo());
        tradeOrderDto.setSelfUserId(order.getPayerUserId());
        tradeOrderDto.setOppositeUserId(order.getPayeeUserId());
        tradeOrderDto.setOrderTitle(String.format("order no:%s", order.getMerchantOrderNo()));
        return tradeOrderDto;
    }

    private RedPacketTradeOrderDto buildRedPacketTradeOrderDto(Order order) {
        RedPacketTradeOrderDto tradeOrderDto = new RedPacketTradeOrderDto();
        tradeOrderDto.setAmount(order.getRedPacketPayAmount());
        tradeOrderDto.setMerchantOrderNo(order.getMerchantOrderNo());
        tradeOrderDto.setSelfUserId(order.getPayerUserId());
        tradeOrderDto.setOppositeUserId(order.getPayeeUserId());
        tradeOrderDto.setOrderTitle(String.format("order no:%s", order.getMerchantOrderNo()));
        return tradeOrderDto;
    }

    @Transactional
    public void confirmMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {
    	logger.info("==================order confirm make payment called==============================");
    	order = orderRepository.findByMerchantOrderNo(order.getMerchantOrderNo());
    	if(null != order && "PAYING".equals(order.getStatus())) {
    		order.confirm();
    		orderRepository.updateOrder(order);
    	}
    }

    @Transactional
    public void cancelMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {
    	logger.info("==================order cancel make payment called================================");
    	order = orderRepository.findByMerchantOrderNo(order.getMerchantOrderNo());
    	if(null != order && "PAYING".equals(order.getStatus())) {
    		order.cancelPayment();
    		orderRepository.updateOrder(order);
    	}
    }

}
