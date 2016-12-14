package com.xuxl.tcctransaction.recover;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuxl.tcctransaction.Transaction;
import com.xuxl.tcctransaction.TransactionRepository;
import com.xuxl.tcctransaction.api.TransactionStatus;
import com.xuxl.tcctransaction.support.TransactionConfigurator;

public class TransactionRecovery {

	private static final Logger logger = LoggerFactory.getLogger(TransactionRecovery.class);

	private TransactionConfigurator transactionConfigurator;

	public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
		this.transactionConfigurator = transactionConfigurator;
	}

	public void startRecover() {

		List<Transaction> transactions = loadErrorTransactions();

		recoverErrorTransactions(transactions);
	}

	private List<Transaction> loadErrorTransactions() {

		TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();

		long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

		List<Transaction> transactions = transactionRepository.findAllUnmodifiedSince(
				new Date(currentTimeInMillis - transactionConfigurator.getRecoverConfig().getRecoverDuration() * 1000));

		List<Transaction> recoverTransactions = new ArrayList<Transaction>();

		for (Transaction transaction : transactions) {

			int result = transactionRepository.update(transaction);

			if (result > 0) {
				recoverTransactions.add(transaction);
			}
		}

		return recoverTransactions;
	}

	private void recoverErrorTransactions(List<Transaction> transactions) {

		for (Transaction transaction : transactions) {
			if (transaction.getRetriedCount() > transactionConfigurator.getRecoverConfig().getMaxRetryCount()) {
				logger.error(String.format(
						"recover failed with max retry count,will not try again. txid:%s, status:%s,retried count:%d",
						transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()));
				continue;
			}
			try {
				transaction.addRetriedCount();
				if (transaction.getStatus().equals(TransactionStatus.CONFIRMING)) {
					transaction.changeStatus(TransactionStatus.CONFIRMING);
					transactionConfigurator.getTransactionRepository().update(transaction);
					transaction.commit();
				} else {
					transaction.changeStatus(TransactionStatus.CANCELLING);
					transactionConfigurator.getTransactionRepository().update(transaction);
					transaction.rollback();
				}

				transactionConfigurator.getTransactionRepository().delete(transaction);
			} catch (Throwable e) {
				logger.warn(String.format("recover failed, txid:%s, status:%s,retried count:%d", transaction.getXid(),
						transaction.getStatus().getId(), transaction.getRetriedCount()), e);
			}
		}
	}

}
