package com.library.music.exceptions;

public class InvalidInputException extends Exception {
    public InvalidInputException(int code, String message) {
        super(message);
        System.out.println(code + " : " + message);
    }
}
