package com.xuxl.tcctransaction;

public class SystemException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SystemException(String message) {
        super(message);
    }

    public SystemException(Throwable e) {
        super(e);
    }

    public SystemException(String message, Throwable e) {
        super(message, e);
    }
}
