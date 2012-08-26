package com.metly.openfire.exception;

import org.apache.commons.httpclient.HttpException;


public class MetlyException extends RuntimeException{

    public MetlyException(String message, Throwable e) {
        super(message);
        initCause(e);
    }

    public MetlyException() {
        super();
    }

    public MetlyException(Throwable e) {
        initCause(e);
    }

    public MetlyException(String string) {
        super(string);
    }
}
