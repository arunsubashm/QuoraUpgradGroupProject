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
import java.util.ArrayList;
import java.util.List;
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
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization") final String authorization, @PathVariable String answerId) throws AuthorizationFailedException,
            UserNotFoundException, AnswerNotFoundException {
        AnswerEntity answerEntity = answerService.deleteAnswer(authorization, answerId);
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


    /**
     * End point to edit answer's
     * @param accessToken
     * @param answerId
     * @param answerEditRequest
     * @return
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@RequestHeader("authorization") final String accessToken,
                                                         @PathVariable("answerId") final String answerId, final AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException {

        //update the details.
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(answerId);
        answerEntity.setAnswer(answerEditRequest.getContent());
        answerEntity.setDate(ZonedDateTime.now());

        final AnswerEntity updatedAnswerEntity = answerService.editAnswer(accessToken, answerId, answerEntity);
        AnswerEditResponse response = new AnswerEditResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(response, HttpStatus.OK);
    }
    /**
     * End point to  get all answers to a particular question
     * @param accessToken
     * @param questionId
     * @return all the answers posted for that particular question from the database
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@RequestHeader("authorization") final String accessToken, @PathVariable("questionId") final String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        List<AnswerDetailsResponse> listAnswerDetailsResponse = new ArrayList<AnswerDetailsResponse>();
        List<AnswerEntity> answerEntityList = answerService.getAllAnswersToQuestion(accessToken, questionId);
        if (answerEntityList != null && !answerEntityList.isEmpty()) {
            for (AnswerEntity answerEntity : answerEntityList) {
                listAnswerDetailsResponse.add(new AnswerDetailsResponse().id(answerEntity.getUuid())
                        .answerContent(answerEntity.getAnswer()).questionContent(answerEntity.getQuestionId().getContent()));
            }
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(listAnswerDetailsResponse, HttpStatus.OK);

    }
}
