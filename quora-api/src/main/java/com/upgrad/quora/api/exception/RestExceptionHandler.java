package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Handles SignUpRestrictedException. Triggered when duplicate username and email found during signup.
     *
     * @param exception SignUpRestrictedException
     * @param request   WebRequest
     * @return ResponseEntity
     */
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.CONFLICT);
    }

    /**
     * Handles UserNotFoundException. Triggered when user to be deleted is not found.
     *
     * @param exception UserNotFoundException
     * @param request   WebRequest
     * @return ResponseEntity
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles AuthorizationFailedException. Triggered when User not found.
     *
     * @param exception AuthorizationFailedException
     * @param request   WebRequest
     * @return ResponseEntity
     */
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles the AutenticationFailedException. This is triggered when user failed to authenticate himself. Bad user name or Password.
     * AuthenticationFailedException - HttpStatus.UNAUTHORIZED
     *
     * @param authFailedException
     * @param request WebRequest
     * @return ResponseEntity
     */

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException authFailedException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(authFailedException.getCode()).message(authFailedException.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /** Handles the SignOutRestrictedException. This is triggered when we do not find a matching JWT Token
     * AuthenticationFailedException - HttpStatus.UNAUTHORIZED
     *
     * @param signOutRestrictedException
     * @param request WebRequest
     * @return ResponseEntity
     */

    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> signOutRestrictedException(SignOutRestrictedException signOutRestrictedException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(signOutRestrictedException.getCode()).message(signOutRestrictedException.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /** Handles bad request that is sent to server
     *
     * @param exception request violation exception
     * @param request
     * @return
     */
    @ExceptionHandler(RequestViolationException.class)
    public ResponseEntity<ErrorResponse> requestViolationException(RequestViolationException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }
}
