package com.xuxl.tcctransaction.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;

import com.xuxl.tcctransaction.interceptor.CompensableTransactionInterceptor;

@Aspect
public class TccCompensableAspect implements Ordered {

    private int order = Ordered.HIGHEST_PRECEDENCE;

    private CompensableTransactionInterceptor compensableTransactionInterceptor;

    @Pointcut("@annotation(com.xuxl.tcctransaction.Compensable)")
    public void compensableService() {
    	
    }
    
    @Around("compensableService()")
    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {
        return compensableTransactionInterceptor.interceptCompensableMethod(pjp);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setCompensableTransactionInterceptor(CompensableTransactionInterceptor compensableTransactionInterceptor) {
        this.compensableTransactionInterceptor = compensableTransactionInterceptor;
    }
}
