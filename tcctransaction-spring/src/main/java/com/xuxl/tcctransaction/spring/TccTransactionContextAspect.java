package com.xuxl.tcctransaction.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;

import com.xuxl.tcctransaction.interceptor.ResourceCoordinatorInterceptor;

@Aspect
public class TccTransactionContextAspect implements Ordered {

    private int order = Ordered.HIGHEST_PRECEDENCE + 1;

    private ResourceCoordinatorInterceptor resourceCoordinatorInterceptor;

    @Pointcut("execution(public * *(com.xuxl.tcctransaction.api.TransactionContext,..))||@annotation(com.xuxl.tcctransaction.Compensable)")
    public void transactionContextCall() {
    	
    }

    @Around("transactionContextCall()")
    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {
        return resourceCoordinatorInterceptor.interceptTransactionContextMethod(pjp);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setResourceCoordinatorInterceptor(ResourceCoordinatorInterceptor resourceCoordinatorInterceptor) {
        this.resourceCoordinatorInterceptor = resourceCoordinatorInterceptor;
    }
}
