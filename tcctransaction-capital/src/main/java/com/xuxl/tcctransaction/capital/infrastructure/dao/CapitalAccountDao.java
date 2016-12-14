package com.xuxl.tcctransaction.capital.infrastructure.dao;

import com.xuxl.tcctransaction.capital.domain.entity.CapitalAccount;

public interface CapitalAccountDao {

    CapitalAccount findByUserId(long userId);

    void update(CapitalAccount capitalAccount);
}
