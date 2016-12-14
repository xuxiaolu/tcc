package com.xuxl.tcctransaction.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.xuxl.tcctransaction.InvocationContext;
import com.xuxl.tcctransaction.Participant;
import com.xuxl.tcctransaction.Terminator;
import com.xuxl.tcctransaction.Transaction;
import com.xuxl.tcctransaction.TransactionRepository;
import com.xuxl.tcctransaction.api.TransactionContext;
import com.xuxl.tcctransaction.api.TransactionStatus;
import com.xuxl.tcctransaction.api.TransactionXid;
import com.xuxl.tcctransaction.support.TransactionConfigurator;
import com.xuxl.tcctransaction.utils.CompensableMethodUtils;

@Activate(group = {Constants.CONSUMER})
public class TccCompensableFilter implements Filter {

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		TransactionConfigurator transactionConfigurator = SpringContextUtil.getBean(TransactionConfigurator.class);
		Transaction transaction = transactionConfigurator.getTransactionManager().getCurrentTransaction();
		if(transaction != null && transaction.getStatus().equals(TransactionStatus.TRYING)) {
			Class<?>[] parameterTypes = invocation.getParameterTypes();
			int position = CompensableMethodUtils.getTransactionContextParamPosition(parameterTypes);
			if(position != -1) {
				String methodName = invocation.getMethodName();
				Object[] args = invocation.getArguments();
				TransactionXid xid = new TransactionXid(transaction.getXid().getGlobalTransactionId());
				args[position] = new TransactionContext(xid, transaction.getStatus().getId());
				Object[] confirmArgs = new Object[args.length];
				Object[] cancelArgs = new Object[args.length];
				System.arraycopy(args, 0, confirmArgs, 0, args.length);
				confirmArgs[position] = new TransactionContext(xid, TransactionStatus.CONFIRMING.getId());
				System.arraycopy(args, 0, cancelArgs, 0, args.length);
				cancelArgs[position] = new TransactionContext(xid, TransactionStatus.CANCELLING.getId());
				Class<?> targetClass = invocation.getInvoker().getInterface();
				InvocationContext confirmInvocation = new InvocationContext(targetClass, methodName, parameterTypes, confirmArgs);
				InvocationContext cancelInvocation = new InvocationContext(targetClass, methodName, parameterTypes, cancelArgs);
				Participant participant = new Participant(xid,new Terminator(confirmInvocation, cancelInvocation));
				transaction.enlistParticipant(participant);
				TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
				transactionRepository.update(transaction);
			}
		}
		return invoker.invoke(invocation);
	}

}
