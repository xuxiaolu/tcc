package com.xuxl.tcctransaction;

import java.util.Date;
import java.util.List;

import com.xuxl.tcctransaction.api.TransactionXid;

public interface TransactionRepository {

    int create(Transaction transaction);

    int update(Transaction transaction);

    int delete(Transaction transaction);

    Transaction findByXid(TransactionXid xid);

    List<Transaction> findAllUnmodifiedSince(Date date);
}
