package com.xuxl.tcctransaction.capital.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xuxl.tcctransaction.capital.api.CapitalAccountService;
import com.xuxl.tcctransaction.capital.domain.repository.CapitalAccountRepository;
import com.xuxl.tcctransaction.dubbo.config.DubboService;

@Component("capitalAccountService")
@DubboService(retries = 0,timeout = 6000,interfaceClass = CapitalAccountService.class)
public class CapitalAccountServiceImpl implements CapitalAccountService {

    @Autowired
    CapitalAccountRepository capitalAccountRepository;

    public BigDecimal getCapitalAccountByUserId(long userId) {
        return capitalAccountRepository.findByUserId(userId).getBalanceAmount();
    }
}
