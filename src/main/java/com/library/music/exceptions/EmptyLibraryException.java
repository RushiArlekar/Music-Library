package com.library.music.exceptions;

public class EmptyLibraryException extends Exception {
    public EmptyLibraryException(int code, String message) {
        super(message);
        System.out.println(code + " : " + message);
    }
}
