package com.library.music.exceptions;

public class DatabaseErrorException extends Exception {
    public DatabaseErrorException(int code, String message) {
        super(message);
        System.out.println(code + " : " + message);
    }
}
