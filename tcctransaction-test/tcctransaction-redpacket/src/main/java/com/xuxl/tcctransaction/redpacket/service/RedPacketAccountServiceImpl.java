package com.xuxl.tcctransaction.redpacket.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xuxl.tcctransaction.dubbo.config.DubboService;
import com.xuxl.tcctransaction.redpacket.api.RedPacketAccountService;
import com.xuxl.tcctransaction.redpacket.domain.repository.RedPacketAccountRepository;

@Component("redPacketAccountService")
@DubboService(retries = 0,timeout = 6000,interfaceClass = RedPacketAccountService.class)
public class RedPacketAccountServiceImpl implements RedPacketAccountService {

    @Autowired
    RedPacketAccountRepository redPacketAccountRepository;

    public BigDecimal getRedPacketAccountByUserId(long userId) {
        return redPacketAccountRepository.findByUserId(userId).getBalanceAmount();
    }
}
