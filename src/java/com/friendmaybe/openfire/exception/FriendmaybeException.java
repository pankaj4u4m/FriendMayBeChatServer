package com.friendmaybe.openfire.exception;

import org.apache.commons.httpclient.HttpException;

public class FriendmaybeException extends RuntimeException {

    public FriendmaybeException(String message, Throwable e) {
        super(message);
        initCause(e);
    }

    public FriendmaybeException() {
        super();
    }

    public FriendmaybeException(Throwable e) {
        initCause(e);
    }

    public FriendmaybeException(String string) {
        super(string);
    }
}
