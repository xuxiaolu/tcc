package com.xuxl.tcctransaction.interceptor;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.xuxl.tcctransaction.Compensable;
import com.xuxl.tcctransaction.InvocationContext;
import com.xuxl.tcctransaction.Participant;
import com.xuxl.tcctransaction.Terminator;
import com.xuxl.tcctransaction.Transaction;
import com.xuxl.tcctransaction.TransactionRepository;
import com.xuxl.tcctransaction.api.TransactionContext;
import com.xuxl.tcctransaction.api.TransactionStatus;
import com.xuxl.tcctransaction.api.TransactionXid;
import com.xuxl.tcctransaction.common.MethodType;
import com.xuxl.tcctransaction.support.TransactionConfigurator;
import com.xuxl.tcctransaction.utils.CompensableMethodUtils;
import com.xuxl.tcctransaction.utils.ReflectionUtils;

public class ResourceCoordinatorInterceptor {

    private TransactionConfigurator transactionConfigurator;

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {
        Transaction transaction = transactionConfigurator.getTransactionManager().getCurrentTransaction();
        if (transaction != null && transaction.getStatus().equals(TransactionStatus.TRYING)) {
            TransactionContext transactionContext = CompensableMethodUtils.getTransactionContextFromArgs(pjp.getArgs());
            Compensable compensable = getCompensable(pjp);
            MethodType methodType = CompensableMethodUtils.calculateMethodType(transactionContext, compensable != null ? true : false);
            switch (methodType) {
                case ROOT:
                    generateAndEnlistRootParticipant(pjp);
                    break;
                case PROVIDER:
                    generateAndEnlistProviderParticipant(pjp);
                    break;
				default:
					break;
            }
        }
        return pjp.proceed(pjp.getArgs());
    }

    private Participant generateAndEnlistRootParticipant(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Compensable compensable = getCompensable(pjp);
        String confirmMethodName = compensable.confirmMethod();
        String cancelMethodName = compensable.cancelMethod();
        Transaction transaction = transactionConfigurator.getTransactionManager().getCurrentTransaction();
        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId());
        Class<?> targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(), method.getName(), method.getParameterTypes());
        InvocationContext confirmInvocation = new InvocationContext(targetClass,
                confirmMethodName,
                method.getParameterTypes(), pjp.getArgs());
        InvocationContext cancelInvocation = new InvocationContext(targetClass,
                cancelMethodName,
                method.getParameterTypes(), pjp.getArgs());
        Participant participant = new Participant(xid,new Terminator(confirmInvocation, cancelInvocation));
        transaction.enlistParticipant(participant);
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        transactionRepository.update(transaction);
        return participant;
    }

    private Participant generateAndEnlistProviderParticipant(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Compensable compensable = getCompensable(pjp);
        String confirmMethodName = compensable.confirmMethod();
        String cancelMethodName = compensable.cancelMethod();
        Transaction transaction = transactionConfigurator.getTransactionManager().getCurrentTransaction();
        TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId());
        Class<?> targetClass = ReflectionUtils.getDeclaringType(pjp.getTarget().getClass(), method.getName(), method.getParameterTypes());
        InvocationContext confirmInvocation = new InvocationContext(targetClass, confirmMethodName,
                method.getParameterTypes(), pjp.getArgs());
        InvocationContext cancelInvocation = new InvocationContext(targetClass, cancelMethodName,
                method.getParameterTypes(), pjp.getArgs());
        Participant participant =
                new Participant(
                        xid,
                        new Terminator(confirmInvocation, cancelInvocation));
        transaction.enlistParticipant(participant);
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        transactionRepository.update(transaction);
        return participant;
    }

    private Compensable getCompensable(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Compensable compensable = method.getAnnotation(Compensable.class);
        if (compensable == null) {
            Method targetMethod = null;
            try {
                targetMethod = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
                if (targetMethod != null) {
                    compensable = targetMethod.getAnnotation(Compensable.class);
                }
            } catch (NoSuchMethodException e) {
                compensable = null;
            }
        }
        return compensable;
    }
}
