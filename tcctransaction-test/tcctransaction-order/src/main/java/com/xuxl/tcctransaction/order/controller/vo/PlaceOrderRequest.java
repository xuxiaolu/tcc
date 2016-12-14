package com.xuxl.tcctransaction.order.controller.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class PlaceOrderRequest {

    private long payerUserId;

    private long shopId;

    private BigDecimal redPacketPayAmount;

    private List<Pair<Long, Integer>> productQuantities = new ArrayList<Pair<Long, Integer>>();

    public long getPayerUserId() {
        return payerUserId;
    }

    public void setPayerUserId(long payerUserId) {
        this.payerUserId = payerUserId;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public BigDecimal getRedPacketPayAmount() {
        return redPacketPayAmount;
    }

    public void setRedPacketPayAmount(BigDecimal redPacketPayAmount) {
        this.redPacketPayAmount = redPacketPayAmount;
    }

    public List<Pair<Long, Integer>> getProductQuantities() {
        return productQuantities;
    }

    public void setProductQuantities(List<Pair<Long, Integer>> productQuantities) {
        this.productQuantities = productQuantities;
    }
}
