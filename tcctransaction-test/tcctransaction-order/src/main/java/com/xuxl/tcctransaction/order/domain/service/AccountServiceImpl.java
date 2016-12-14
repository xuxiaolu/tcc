package com.xuxl.tcctransaction.order.domain.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.xuxl.tcctransaction.capital.api.CapitalAccountService;
import com.xuxl.tcctransaction.dubbo.config.DubboReference;
import com.xuxl.tcctransaction.redpacket.api.RedPacketAccountService;

@Service("accountService")
public class AccountServiceImpl {

	@DubboReference
    RedPacketAccountService redPacketAccountService;
    
	@DubboReference
    CapitalAccountService capitalAccountService;


    public BigDecimal getRedPacketAccountByUserId(long userId){
        return redPacketAccountService.getRedPacketAccountByUserId(userId);
    }

    public BigDecimal getCapitalAccountByUserId(long userId){
        return capitalAccountService.getCapitalAccountByUserId(userId);
    }
}
