package com.claudev.agenda.exception;

public class TokenExpiredException extends  RuntimeException{

    public TokenExpiredException(String message) {
        super(message);
    }
}
