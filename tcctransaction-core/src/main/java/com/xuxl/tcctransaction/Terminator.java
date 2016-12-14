package com.xuxl.tcctransaction;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.xuxl.tcctransaction.support.BeanFactoryAdapter;
import com.xuxl.tcctransaction.utils.StringUtils;

public class Terminator implements Serializable {

	private static final long serialVersionUID = 1L;

	private InvocationContext confirmInvocationContext;

    private InvocationContext cancelInvocationContext;

    public Terminator() {

    }

    public Terminator(InvocationContext confirmInvocationContext, InvocationContext cancelInvocationContext) {
        this.confirmInvocationContext = confirmInvocationContext;
        this.cancelInvocationContext = cancelInvocationContext;
    }

    public void commit() {
        invoke(confirmInvocationContext);
    }

    public void rollback() {
        invoke(cancelInvocationContext);
    }

    private Object invoke(InvocationContext invocationContext) {
        if (StringUtils.isNotEmpty(invocationContext.getMethodName())) {
            try {
                Object target = BeanFactoryAdapter.getBean(invocationContext.getTargetClass());
                if (target == null && !invocationContext.getTargetClass().isInterface()) {
                    target = invocationContext.getTargetClass().newInstance();
                }
                Method method = null;
                method = target.getClass().getMethod(invocationContext.getMethodName(), invocationContext.getParameterTypes());
                return method.invoke(target, invocationContext.getArgs());
            } catch (Exception e) {
                throw new SystemException(e);
            }
        }
        return null;
    }
}
