package com.xuxl.tcctransaction.spring.support;

import org.springframework.beans.factory.annotation.Autowired;

import com.xuxl.tcctransaction.TransactionManager;
import com.xuxl.tcctransaction.TransactionRepository;
import com.xuxl.tcctransaction.recover.RecoverConfig;
import com.xuxl.tcctransaction.spring.recover.DefaultRecoverConfig;
import com.xuxl.tcctransaction.support.TransactionConfigurator;

public class TccTransactionConfigurator implements TransactionConfigurator {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired(required = false)
    private RecoverConfig recoverConfig = DefaultRecoverConfig.INSTANCE;

    private TransactionManager transactionManager = new TransactionManager(this);

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public RecoverConfig getRecoverConfig() {
        return recoverConfig;
    }
}
