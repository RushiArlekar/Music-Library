package com.library.music.exceptions;

public class NoMusicFoundException extends Exception {
    public NoMusicFoundException(int code, String message) {
        super(message);
        System.out.println(code + " : " + message);
    }

}
