package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionsService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserDao userDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(final String accessToken) throws  AuthorizationFailedException  {
        UserAuthTokenEntity authTokenEntity =  authenticationService.getUserByToken(accessToken);
        if (authTokenEntity != null) {
            List<QuestionEntity> questions = questionDao.getAllQuestions();
            return questions;
        }
        else
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity authTokenEntity =  authenticationService.getUserByToken(accessToken);
        /**
         * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException'
         */
        if (authTokenEntity == null) {
            throw  new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(accessToken);
        /**
         * If the user has signed out, throw 'AuthorizationFailedException'
         */
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }
        /**
         * If the user with uuid whose questions are to be retrieved from the database does not exist in the database, throw 'UserNotFoundException'
         */

        if (userDao.getUserEntityById(userId) == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        List<QuestionEntity> questionsList = questionDao.getAllQuestionsByUser(userId);

        return questionsList;
    }
}
