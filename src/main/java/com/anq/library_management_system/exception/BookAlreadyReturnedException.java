package com.anq.library_management_system.exception;

public class BookAlreadyReturnedException extends RuntimeException{
    public BookAlreadyReturnedException(String message) {
        super(message); }
}
