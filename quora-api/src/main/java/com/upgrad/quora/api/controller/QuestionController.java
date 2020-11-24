package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionsService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

public class QuestionController {

    @Autowired
    private QuestionsService questionsService;
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserDao userDao;

    /** Implementation of GetAllQuestions endpoint.
     * This method expose end point /question/all
     *
     * @param authorization : authorization token.
     * @return List<QuestionDetailsResponse>
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {

        authenticationService.getUserByToken(accessToken);

        List<QuestionEntity> allQuestions = questionsService.getAllQuestions();

        return getListResponseEntity(allQuestions);
    }

    /** Handled endpoint to display all questions. Prepares the List of All question to be displayed.
     * This method expose end point /question/all
     *
     * @param allQuestion : List of QuestionEntity
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

    /** Implementation of GetAllQuestions endpoint.
     * This method expose end point /question/all
     *
     * @param authorization : authorization token.
     * @return List<QuestionDetailsResponse>
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionByUser(@RequestHeader("authorization") final String accessToken, @PathVariable("userId") final String userId) throws  AuthorizationFailedException {

        UserAuthTokenEntity authTokenEntity =  authenticationService.getUserByToken(accessToken);
        if (authTokenEntity != null) {

            List<QuestionEntity> allQuestionsByUser = questionsService.getAllQuestionsByUser(userId);

            return getListResponseEntity(allQuestionsByUser);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
