package com.xuxl.tcctransaction.spring.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.xa.Xid;

import org.springframework.beans.factory.annotation.Autowired;

import com.xuxl.tcctransaction.Transaction;
import com.xuxl.tcctransaction.repository.CachableTransactionRepository;
import com.xuxl.tcctransaction.serializer.KryoTransactionSerializer;
import com.xuxl.tcctransaction.serializer.ObjectSerializer;
import com.xuxl.tcctransaction.spring.domain.TccTransactionCriteria;
import com.xuxl.tcctransaction.spring.domain.TccTransactionCriteria.Criteria;
import com.xuxl.tcctransaction.spring.domain.TccTransactionWithBLOBs;
import com.xuxl.tcctransaction.spring.mapper.TccTransactionMapper;
import com.xuxl.tcctransaction.utils.CollectionUtils;
import com.xuxl.tcctransaction.utils.StringUtils;

public class SpringJdbcTransactionRepository extends CachableTransactionRepository {
	
	private ObjectSerializer<Transaction> serializer = new KryoTransactionSerializer<Transaction>();
	
	private String domain;
	
	@Autowired
	private TccTransactionMapper tccTransactionMapper;
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

    protected int doCreate(Transaction transaction) {
    	TccTransactionWithBLOBs tccTransaction = new TccTransactionWithBLOBs();
    	tccTransaction.setGlobalTxId(transaction.getXid().getGlobalTransactionId());
    	tccTransaction.setBranchQualifier(transaction.getXid().getBranchQualifier());
    	tccTransaction.setTransactionType(transaction.getTransactionType().getId());
    	tccTransaction.setContent(serializer.serialize(transaction));
    	tccTransaction.setStatus(transaction.getStatus().getId());
    	tccTransaction.setRetriedCount(transaction.getRetriedCount());
    	tccTransaction.setCreateTime(transaction.getCreateTime());
    	tccTransaction.setLastUpdateTime(transaction.getLastUpdateTime());
    	tccTransaction.setVersion((int)transaction.getVersion());
    	tccTransaction.setDomain(domain);
    	return tccTransactionMapper.insertSelective(tccTransaction);
    }
    
	protected int doUpdate(Transaction transaction) {
		transaction.updateTime();
		transaction.updateVersion();
		TccTransactionWithBLOBs tccTransaction = new TccTransactionWithBLOBs();
		tccTransaction.setContent(serializer.serialize(transaction));
		tccTransaction.setStatus(transaction.getStatus().getId());
		tccTransaction.setLastUpdateTime(transaction.getLastUpdateTime());
		tccTransaction.setRetriedCount(transaction.getRetriedCount());
		tccTransaction.setVersion((int)transaction.getVersion());
		TccTransactionCriteria tccTransactionCriteria = new TccTransactionCriteria();
		Criteria criteria = tccTransactionCriteria.createCriteria();
		criteria.addCriterion("GLOBAL_TX_ID = ", transaction.getXid().getGlobalTransactionId(), "globalTxId");
		criteria.addCriterion("BRANCH_QUALIFIER = ", transaction.getXid().getBranchQualifier(), "branchQualifier");
		criteria.andVersionEqualTo((int)transaction.getVersion() - 1);
		if(StringUtils.isNotEmpty(domain)) {
			criteria.andDomainEqualTo(domain);
		}
		return tccTransactionMapper.updateByExampleSelective(tccTransaction, tccTransactionCriteria);
	}

	protected int doDelete(Transaction transaction) {
		TccTransactionCriteria tccTransactionCriteria = new TccTransactionCriteria();
		Criteria criteria = tccTransactionCriteria.createCriteria();
		criteria.addCriterion("GLOBAL_TX_ID = ", transaction.getXid().getGlobalTransactionId(), "globalTxId");
		criteria.addCriterion("BRANCH_QUALIFIER = ", transaction.getXid().getBranchQualifier(), "branchQualifier");
		if(StringUtils.isNotEmpty(domain)) {
			criteria.andDomainEqualTo(domain);
		}
		return tccTransactionMapper.deleteByExample(tccTransactionCriteria);
	}

	protected Transaction doFindOne(Xid xid) {
		List<Transaction> transactions = doFind(Arrays.asList(xid));
		if (!CollectionUtils.isEmpty(transactions)) {
			return transactions.get(0);
		}
		return null;
	}

	@Override
	protected List<Transaction> doFindAllUnmodifiedSince(java.util.Date date) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		TccTransactionCriteria tccTransactionCriteria = new TccTransactionCriteria();
		Criteria criteria = tccTransactionCriteria.createCriteria();
		criteria.andLastUpdateTimeLessThan(date).andTransactionTypeEqualTo(1);
		if(StringUtils.isNotEmpty(domain)) {
			criteria.andDomainEqualTo(domain);
		}
		List<TccTransactionWithBLOBs> tccTransactionList = tccTransactionMapper.selectByExampleWithBLOBs(tccTransactionCriteria);
		for(TccTransactionWithBLOBs tccTransaction : tccTransactionList) {
			Transaction transaction = (Transaction) serializer.deserialize(tccTransaction.getContent());
			transaction.setLastUpdateTime(tccTransaction.getLastUpdateTime());
			transaction.setVersion(tccTransaction.getVersion());
			transaction.resetRetriedCount(tccTransaction.getRetriedCount());
			transactions.add(transaction);
		}
		return transactions;
	}

	protected List<Transaction> doFind(List<Xid> xids) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		if (CollectionUtils.isEmpty(xids)) {
			return transactions;
		}
		TccTransactionCriteria tccTransactionCriteria = new TccTransactionCriteria();
		for (Xid xid : xids) {
			Criteria criteria = tccTransactionCriteria.or();
			criteria.addCriterion("GLOBAL_TX_ID = ", xid.getGlobalTransactionId(), "globalTxId");
			criteria.addCriterion("BRANCH_QUALIFIER = ", xid.getBranchQualifier(), "branchQualifier");
			if(StringUtils.isNotEmpty(domain)) {
				criteria.andDomainEqualTo(domain);
			}
		}
		List<TccTransactionWithBLOBs> tccTransactionList = tccTransactionMapper.selectByExampleWithBLOBs(tccTransactionCriteria);
		for(TccTransactionWithBLOBs tccTransaction : tccTransactionList) {
			Transaction transaction = (Transaction) serializer.deserialize(tccTransaction.getContent());
			transaction.setLastUpdateTime(tccTransaction.getLastUpdateTime());
			transaction.setVersion(tccTransaction.getVersion());
			transaction.resetRetriedCount(tccTransaction.getRetriedCount());
			transactions.add(transaction);
		}
		return transactions;
	}
}
