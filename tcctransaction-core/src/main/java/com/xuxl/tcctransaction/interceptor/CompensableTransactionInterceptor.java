package com.xuxl.tcctransaction.interceptor;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxl.tcctransaction.NoExistedTransactionException;
import com.xuxl.tcctransaction.OptimisticLockException;
import com.xuxl.tcctransaction.api.TransactionContext;
import com.xuxl.tcctransaction.api.TransactionStatus;
import com.xuxl.tcctransaction.common.MethodType;
import com.xuxl.tcctransaction.support.TransactionConfigurator;
import com.xuxl.tcctransaction.utils.CompensableMethodUtils;
import com.xuxl.tcctransaction.utils.ReflectionUtils;

public class CompensableTransactionInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CompensableTransactionInterceptor.class);

    private TransactionConfigurator transactionConfigurator;

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {
        TransactionContext transactionContext = CompensableMethodUtils.getTransactionContextFromArgs(pjp.getArgs());
        MethodType methodType = CompensableMethodUtils.calculateMethodType(transactionContext, true);
        switch (methodType) {
            case ROOT:
                return rootMethodProceed(pjp);
            case PROVIDER:
                return providerMethodProceed(pjp, transactionContext);
            default:
                return pjp.proceed();
        }
    }

    private Object rootMethodProceed(ProceedingJoinPoint pjp) throws Throwable {
        transactionConfigurator.getTransactionManager().begin();
        Object returnValue = null;
        try {
            returnValue = pjp.proceed();
        } catch (OptimisticLockException e) {
            throw e; //do not rollback, waiting for recovery job
        } catch (Throwable tryingException) {
            logger.warn("compensable transaction trying failed.", tryingException);
            transactionConfigurator.getTransactionManager().rollback();
            throw tryingException;
        }
        transactionConfigurator.getTransactionManager().commit();
        return returnValue;
    }

    private Object providerMethodProceed(ProceedingJoinPoint pjp, TransactionContext transactionContext) throws Throwable {
        switch (TransactionStatus.valueOf(transactionContext.getStatus())) {
            case TRYING:
                transactionConfigurator.getTransactionManager().propagationNewBegin(transactionContext);
                return pjp.proceed();
            case CONFIRMING:
                try {
                    transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                    transactionConfigurator.getTransactionManager().commit();
                } catch (NoExistedTransactionException excepton) {
                    //the transaction has been commit,ignore it.
                }
                break;
            case CANCELLING:
                try {
                    transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                    transactionConfigurator.getTransactionManager().rollback();
                } catch (NoExistedTransactionException exception) {
                    //the transaction has been rollback,ignore it.
                }
                break;
        }
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();
        return ReflectionUtils.getNullValue(method.getReturnType());
    }

}
