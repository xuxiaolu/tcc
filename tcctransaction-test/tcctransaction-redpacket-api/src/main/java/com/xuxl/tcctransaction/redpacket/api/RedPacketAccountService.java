package com.xuxl.tcctransaction.redpacket.api;

import java.math.BigDecimal;

public interface RedPacketAccountService {
    BigDecimal getRedPacketAccountByUserId(long userId);
}
