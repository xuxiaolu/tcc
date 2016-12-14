package com.xuxl.tcctransaction;

public class CancellingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CancellingException(Throwable cause) {
        super(cause);
    }
}
