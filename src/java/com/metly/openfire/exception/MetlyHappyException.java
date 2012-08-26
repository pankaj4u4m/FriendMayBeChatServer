package com.metly.openfire.exception;

public class MetlyHappyException extends RuntimeException{
	public MetlyHappyException(String message, Throwable e) {
        super(message);
        initCause(e);
    }

    public MetlyHappyException() {
        super();
    }

    public MetlyHappyException(Throwable e) {
        initCause(e);
    }

    public MetlyHappyException(String string) {
        super(string);
    }
}
