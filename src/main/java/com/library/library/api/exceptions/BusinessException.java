package com.library.library.api.exceptions;

import com.library.library.api.model.Book;

public class BusinessException extends RuntimeException {
    public BusinessException(String s) {
        super(s);
    }
}
