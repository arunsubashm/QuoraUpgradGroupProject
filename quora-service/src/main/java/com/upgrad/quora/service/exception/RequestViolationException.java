package com.upgrad.quora.service.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * RequestViolationException is thrown when bad request is sent.
 */
public class RequestViolationException extends Exception {
    private final String code;
    private final String errorMessage;

    public RequestViolationException(String code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }



    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
