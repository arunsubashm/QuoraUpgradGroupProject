package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.RequestViolationException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    AnswerService answerService;
    @Autowired
    AuthenticationService authenticationService;

    /** end point for /answer/delete/{answerId}.
     * user can delete answer based on AnswerID
     * @param authorization
     * @param answerId
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     * @throws AnswerNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteUser(@RequestHeader("authorization") final String authorization, @PathVariable String answerId) throws AuthorizationFailedException,
            UserNotFoundException, AnswerNotFoundException {

        final UserAuthTokenEntity userAuthDetails = authenticationService.getAuthToken(authorization, "User has not signed in", "User is signed out.Sign in first to post an answer");
        AnswerEntity answerEntity = answerService.getAnswerDetailsByAnswerId(answerId);
        answerService.validateTheRequestToDeleteAnswer(userAuthDetails, answerEntity);
        answerService.deleteQuestion(answerId);
        final AnswerDeleteResponse answerDeleted = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleted, HttpStatus.OK);
    }
}