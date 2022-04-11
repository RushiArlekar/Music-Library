package com.library.music.exceptions;

public class NullInputException extends Exception {
    public NullInputException(int code, String message) {
        super(message);
        System.out.println(code + " : " + message);
    }
}
