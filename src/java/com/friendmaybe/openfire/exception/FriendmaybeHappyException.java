package com.friendmaybe.openfire.exception;

public class FriendmaybeHappyException extends RuntimeException {
    public FriendmaybeHappyException(String message, Throwable e) {
        super(message);
        initCause(e);
    }

    public FriendmaybeHappyException() {
        super();
    }

    public FriendmaybeHappyException(Throwable e) {
        initCause(e);
    }

    public FriendmaybeHappyException(String string) {
        super(string);
    }
}
