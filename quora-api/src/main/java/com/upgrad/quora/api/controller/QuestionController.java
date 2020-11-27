package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionsService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.RequestViolationException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.aspectj.weaver.patterns.TypePatternQuestions;
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
public class QuestionController {

    @Autowired
    private QuestionsService questionsService;
    @Autowired
    private AuthenticationService authenticationService;


    /** Implementation of GetAllQuestions endpoint.
     * This method expose end point /question/all
     *
     * @param accessToken : authorization token.
     * @return List<QuestionDetailsResponse>
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {

        List<QuestionEntity> allQuestions = questionsService.getAllQuestions(accessToken);
        return getListResponseEntity(allQuestions);

    }

    /** Handled endpoint to display all questions. Prepares the List of All question to be displayed.
     * This method expose end point /question/all
     *
     * @param allQuestions : List of QuestionEntity
     * @return ResponseEntity
     *
     */
    private ResponseEntity<List<QuestionDetailsResponse>> getListResponseEntity(List<QuestionEntity> allQuestions) {
        QuestionEntity questionEntity;
        if (allQuestions.size() != 0) {
            List<QuestionDetailsResponse> displayAllQuestions = new ArrayList<>();
            for (int i = 0; i < allQuestions.size(); i++) {
                questionEntity = allQuestions.get(i);
                QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
                displayAllQuestions.add(questionDetailsResponse);
            }
            return new ResponseEntity<List<QuestionDetailsResponse>>(displayAllQuestions, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /** This endpoint is implemented to fetch all the questions posed by a specific user. Any user can access this endpoint
     * This method expose end point question/all/{userId}
     *
     * @param accessToken : authorization token.
     * @param userId : UUID of the User.
     * @return List<QuestionDetailsResponse>
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionByUser(@RequestHeader("authorization") final String accessToken, @PathVariable("userId") final String userId) throws AuthorizationFailedException, UserNotFoundException {

        List<QuestionEntity> allQuestionsByUser = questionsService.getAllQuestionsByUser(userId, accessToken);
        return getListResponseEntity(allQuestionsByUser);

    }
    /** This endpoint is used to delete a question that has been posted by a user.
     * the question owner of the question or admin can delete a question.
     * @Param accessToken : Authorization token of the logged in user
     * @Param questionId : ID of the Question.
     * @returns : ResponseEntity<QuestionDeletedResponse>
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

    @RequestMapping(method = RequestMethod.DELETE,path = "question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion (@RequestHeader("authorization") final String accessToken, @PathVariable("questionId") final String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        String deletedQuestionUuid = questionsService.deleteQuestion(accessToken, questionId);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(deletedQuestionUuid).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    /**This endpoint is implemented to create new question in Quora
     *
     * @param accessToken
     * @param questionRequest
     * @return ResponseEntity
     * @throws AuthorizationFailedException, RequestViolationException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String accessToken,
                                                           final QuestionRequest questionRequest) throws AuthorizationFailedException, RequestViolationException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUuid(UUID.randomUUID().toString());
        QuestionEntity savedQuestion = questionsService.createQuestion(accessToken, questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(savedQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> updateQuestion(@PathVariable("questionId") final String id, @RequestHeader("authorization") final String accessToken,
                                                               final QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setContent(questionEditRequest.getContent());
        questionEntity.setUuid(id);

        questionsService.updateQuestion(accessToken, questionEntity);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(id).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);

    }
}
