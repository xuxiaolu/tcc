package com.xuxl.tcctransaction.repository;

public class TransactionIOException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TransactionIOException(String message) {
        super(message);
    }

    public TransactionIOException(Throwable e) {
        super(e);
    }
}
