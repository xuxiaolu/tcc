package com.xuxl.tcctransaction.utils;

import com.xuxl.tcctransaction.api.TransactionContext;
import com.xuxl.tcctransaction.common.MethodType;

public class CompensableMethodUtils {

    public static MethodType calculateMethodType(TransactionContext transactionContext, boolean isCompensable) {

        if (transactionContext == null && isCompensable) {
            //isRootTransactionMethod
            return MethodType.ROOT;
        } else if (transactionContext == null && !isCompensable) {
            //isSoaConsumer
            return MethodType.CONSUMER;
        } else if (transactionContext != null && isCompensable) {
            //isSoaProvider
            return MethodType.PROVIDER;
        } else {
            return MethodType.NORMAL;
        }
    }

    public static int getTransactionContextParamPosition(Class<?>[] parameterTypes) {
        int j = -1;
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].equals(TransactionContext.class)) {
            	j = i;
                break;
            }
        }
        return j;
    }

    public static TransactionContext getTransactionContextFromArgs(Object[] args) {
        TransactionContext transactionContext = null;
        for (Object arg : args) {
            if (arg != null && TransactionContext.class.isAssignableFrom(arg.getClass())) {
                transactionContext = (TransactionContext) arg;
            }
        }
        return transactionContext;
    }
    
    public static TransactionContext getTransactionContextFromMethod(Object[] args) {
    	TransactionContext transactionContext = null;
    	for (Object arg : args) {
    		if (arg != null && TransactionContext.class.isAssignableFrom(arg.getClass())) {
    			transactionContext = (TransactionContext) arg;
    		}
    	}
    	return transactionContext;
    }
}
