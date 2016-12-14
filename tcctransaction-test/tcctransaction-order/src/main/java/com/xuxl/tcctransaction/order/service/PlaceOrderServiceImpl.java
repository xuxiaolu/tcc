package com.xuxl.tcctransaction.order.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xuxl.tcctransaction.CancellingException;
import com.xuxl.tcctransaction.ConfirmingException;
import com.xuxl.tcctransaction.order.domain.entity.Order;
import com.xuxl.tcctransaction.order.domain.entity.Shop;
import com.xuxl.tcctransaction.order.domain.repository.ShopRepository;
import com.xuxl.tcctransaction.order.domain.service.OrderServiceImpl;
import com.xuxl.tcctransaction.order.domain.service.PaymentServiceImpl;

@Service
public class PlaceOrderServiceImpl {

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    PaymentServiceImpl paymentService;

    public String placeOrder(long payerUserId, long shopId, List<Pair<Long, Integer>> productQuantities, BigDecimal redPacketPayAmount) {
        Shop shop = shopRepository.findById(shopId);
        Order order = orderService.createOrder(payerUserId, shop.getOwnerUserId(), productQuantities);
        try {
            paymentService.makePayment(order, redPacketPayAmount, order.getTotalAmount().subtract(redPacketPayAmount));

        } catch (ConfirmingException confirmingException) {
            //exception throws with the tcc transaction status is CONFIRMING,
            //when tcc transaction is confirming status,
            // the tcc transaction recovery will try to confirm the whole transaction to ensure eventually consistent.

        } catch (CancellingException cancellingException) {
            //exception throws with the tcc transaction status is CANCELLING,
            //when tcc transaction is under CANCELLING status,
            // the tcc transaction recovery will try to cancel the whole transaction to ensure eventually consistent.
        } catch (Throwable e) {
            //other exceptions throws at TRYING stage.
            //you can retry or cancel the operation.
        }

        return order.getMerchantOrderNo();
    }
}
