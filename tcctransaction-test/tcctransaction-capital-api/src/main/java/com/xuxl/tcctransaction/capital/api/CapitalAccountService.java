package com.xuxl.tcctransaction.capital.api;

import java.math.BigDecimal;

public interface CapitalAccountService {

    BigDecimal getCapitalAccountByUserId(long userId);
}
