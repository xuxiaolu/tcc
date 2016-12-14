package com.xuxl.tcctransaction.support;

import com.xuxl.tcctransaction.TransactionManager;
import com.xuxl.tcctransaction.TransactionRepository;
import com.xuxl.tcctransaction.recover.RecoverConfig;

public interface TransactionConfigurator {

    public TransactionManager getTransactionManager();

    public TransactionRepository getTransactionRepository();

    public RecoverConfig getRecoverConfig();

}
