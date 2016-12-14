package com.xuxl.tcctransaction.redpacket.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.xuxl.tcctransaction.redpacket.domain.entity.RedPacketAccount;
import com.xuxl.tcctransaction.redpacket.infrastructure.dao.RedPacketAccountDao;

/**
 * Created by changming.xie on 4/2/16.
 */
@Repository
public class RedPacketAccountRepository {

    @Autowired
    RedPacketAccountDao redPacketAccountDao;

    public RedPacketAccount findByUserId(long userId) {

        return redPacketAccountDao.findByUserId(userId);
    }

    public void save(RedPacketAccount redPacketAccount) {
        redPacketAccountDao.update(redPacketAccount);
    }
}
