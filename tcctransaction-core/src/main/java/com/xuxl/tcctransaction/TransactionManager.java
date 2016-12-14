package com.xuxl.tcctransaction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxl.tcctransaction.api.TransactionContext;
import com.xuxl.tcctransaction.api.TransactionStatus;
import com.xuxl.tcctransaction.common.TransactionType;
import com.xuxl.tcctransaction.support.TransactionConfigurator;

public class TransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    private TransactionConfigurator transactionConfigurator;

    public TransactionManager(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    private ThreadLocal<Transaction> threadLocalTransaction = new ThreadLocal<Transaction>();

    public void begin() {
        Transaction transaction = new Transaction(TransactionType.ROOT);
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        transactionRepository.create(transaction);
        threadLocalTransaction.set(transaction);
    }

    public void propagationNewBegin(TransactionContext transactionContext) {
        Transaction transaction = new Transaction(transactionContext);
        transactionConfigurator.getTransactionRepository().create(transaction);
        threadLocalTransaction.set(transaction);
    }

    public void propagationExistBegin(TransactionContext transactionContext) throws NoExistedTransactionException {
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        Transaction transaction = transactionRepository.findByXid(transactionContext.getXid());
        if (transaction != null) {
            transaction.changeStatus(TransactionStatus.valueOf(transactionContext.getStatus()));
            threadLocalTransaction.set(transaction);
        } else {
            throw new NoExistedTransactionException();
        }
    }

    public void commit() {
        Transaction transaction = getCurrentTransaction();
        transaction.changeStatus(TransactionStatus.CONFIRMING);
        transactionConfigurator.getTransactionRepository().update(transaction);
        try {
            transaction.commit();
            transactionConfigurator.getTransactionRepository().delete(transaction);
            threadLocalTransaction.remove();
        } catch (Throwable commitException) {
            logger.error("compensable transaction confirm failed.", commitException);
            throw new ConfirmingException(commitException);
        }
    }

    public Transaction getCurrentTransaction() {
        return threadLocalTransaction.get();
    }

    public void rollback() {
        Transaction transaction = getCurrentTransaction();
        transaction.changeStatus(TransactionStatus.CANCELLING);
        transactionConfigurator.getTransactionRepository().update(transaction);
        try {
            transaction.rollback();
            transactionConfigurator.getTransactionRepository().delete(transaction);
            threadLocalTransaction.remove();
        } catch (Throwable rollbackException) {
            logger.error("compensable transaction rollback failed.", rollbackException);
            throw new CancellingException(rollbackException);
        }
    }
}
