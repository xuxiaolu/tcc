package com.xuxl.tcctransaction;

import java.io.Serializable;

import com.xuxl.tcctransaction.api.TransactionXid;

public class Participant implements Serializable {

	private static final long serialVersionUID = 1L;

	private TransactionXid xid;

    private Terminator terminator;

    public Participant() {

    }

    public Participant(TransactionXid xid, Terminator terminator) {
        this.xid = xid;
        this.terminator = terminator;
    }
    
    public TransactionXid getXid() {
		return xid;
	}

	public void setXid(TransactionXid xid) {
		this.xid = xid;
	}

	public void rollback() {
        terminator.rollback();
    }

    public void commit() {
        terminator.commit();
    }

}
