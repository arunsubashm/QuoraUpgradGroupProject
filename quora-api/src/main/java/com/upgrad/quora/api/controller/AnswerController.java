package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

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
        AnswerEntity answerEntity = answerService.deleteQuestion(authorization, answerId);
        final AnswerDeleteResponse answerDeleted = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleted, HttpStatus.OK);
    }

    /**This endpoint is implemented to create new answer /question/{questionId}/answer/create in Quora
     *
     * @param accessToken, Question ID
     * @param answerRequest
     * @return ResponseEntity <AnswerResponse></AnswerResponse>
     * @throws AuthorizationFailedException, RequestViolationException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String id, @RequestHeader("authorization") final String accessToken,
                                                       final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = new AnswerEntity();

        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUuid(UUID.randomUUID().toString());

        answerService.createAnswer(accessToken, id, answerEntity);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }
}