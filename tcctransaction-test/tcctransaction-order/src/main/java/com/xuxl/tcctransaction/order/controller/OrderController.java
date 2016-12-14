package com.xuxl.tcctransaction.order.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xuxl.tcctransaction.order.controller.vo.PlaceOrderRequest;
import com.xuxl.tcctransaction.order.domain.entity.Product;
import com.xuxl.tcctransaction.order.domain.repository.ProductRepository;
import com.xuxl.tcctransaction.order.domain.service.AccountServiceImpl;
import com.xuxl.tcctransaction.order.domain.service.OrderServiceImpl;
import com.xuxl.tcctransaction.order.service.PlaceOrderServiceImpl;

@Controller
@RequestMapping("")
public class OrderController {

    @Autowired
    PlaceOrderServiceImpl placeOrderService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AccountServiceImpl accountService;

    @Autowired
    OrderServiceImpl orderService;

    @RequestMapping(value = "/user/{userId}/shop/{shopId}",method = RequestMethod.GET)
    public ModelAndView getProductsInShop(@PathVariable long userId,
                                          @PathVariable long shopId){
        List<Product> products = productRepository.findByShopId(shopId);
        ModelAndView mv = new ModelAndView("/shop");
        mv.addObject("products",products);
        mv.addObject("userId",userId);
        mv.addObject("shopId",shopId);

        return mv;
    }

    @RequestMapping(value = "/user/{userId}/shop/{shopId}/product/{productId}/confirm",method = RequestMethod.GET)
    public ModelAndView productDetail(@PathVariable long userId,
                                      @PathVariable long shopId,
                                      @PathVariable long productId){
        ModelAndView mv = new ModelAndView("product_detail");
        mv.addObject("capitalAmount",accountService.getCapitalAccountByUserId(userId));
        mv.addObject("redPacketAmount",accountService.getRedPacketAccountByUserId(userId));
        mv.addObject("product",productRepository.findById(productId));
        mv.addObject("userId",userId);
        mv.addObject("shopId",shopId);
        return mv;
    }

    @RequestMapping(value = "/placeorder", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> placeOrder(@RequestParam String redPacketPayAmount,
                                   @RequestParam long shopId,
                                   @RequestParam long payerUserId,
                                   @RequestParam long productId) {
    	Map<String,Object> result = new HashMap<>();
        PlaceOrderRequest request = buildRequest(redPacketPayAmount,shopId,payerUserId,productId);
        String merchantOrderNo = placeOrderService.placeOrder(request.getPayerUserId(), request.getShopId(),
                request.getProductQuantities(), request.getRedPacketPayAmount());
        String payResultTip = null;
        String status = orderService.getOrderStatusByMerchantOrderNo(merchantOrderNo);
        if("CONFIRMED".equals(status))
            payResultTip = "支付成功";
        else if("PAY_FAILED".equals(status))
            payResultTip = "支付失败";
        result.put("payResult",payResultTip);
        result.put("product",productRepository.findById(productId));
        result.put("capitalAmount",accountService.getCapitalAccountByUserId(payerUserId));
        result.put("redPacketAmount",accountService.getRedPacketAccountByUserId(payerUserId));
        return result;
    }


    private PlaceOrderRequest buildRequest(String redPacketPayAmount,long shopId,long payerUserId,long productId) {
        BigDecimal redPacketPayAmountInBigDecimal = new BigDecimal(redPacketPayAmount);
        if(redPacketPayAmountInBigDecimal.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidParameterException("invalid red packet amount :" + redPacketPayAmount);

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setPayerUserId(payerUserId);
        request.setShopId(shopId);
        request.setRedPacketPayAmount(new BigDecimal(redPacketPayAmount));
        request.getProductQuantities().add(new ImmutablePair<Long, Integer>(productId, 1));
        return request;
    }
}
