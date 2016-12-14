package com.xuxl.tcctransaction.redpacket.infrastructure.dao;

import com.xuxl.tcctransaction.redpacket.domain.entity.RedPacketAccount;

public interface RedPacketAccountDao {

    RedPacketAccount findByUserId(long userId);

    void update(RedPacketAccount redPacketAccount);
}
