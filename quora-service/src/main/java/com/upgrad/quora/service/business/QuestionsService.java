package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.*;
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

         // Check If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException''

        if (authTokenEntity == null) {
            throw  new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(accessToken);

         // Check If the user has signed out, if yes throw 'AuthorizationFailedException' with the message code-'ATHR-002'
        // and message-'User is signed out.Sign in first to get all questions posted by a specific user'

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

         // Check if the user with uuid whose questions are to be retrieved from the database does not exist in the database, throw 'UserNotFoundException''

        if (userDao.getUserEntityById(userId) == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        List<QuestionEntity> questionsList = questionDao.getAllQuestionsByUser(userId);

        return questionsList;
    }

    /**
     *
     * @param accessToken
     * @param questionEntity contains de
     * @return
     * @throws AuthorizationFailedException
     * @throws RequestViolationException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(String accessToken, QuestionEntity questionEntity) throws AuthorizationFailedException, RequestViolationException {
        UserAuthTokenEntity authTokenEntity = authenticationService.getUserByToken(accessToken);
        // Throws custom exception when content(i.e question ) us empty
        if (null == questionEntity.getContent()){
            throw new RequestViolationException("IAE_001", "content cant be empty");
        }

        // Check If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException''
        if (authTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        //fetch user by auth token
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(accessToken);
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }
        //save the question
        questionEntity.setUserId(userAuthTokenEntity.getUser());
        return questionDao.saveQuestion(questionEntity);
    }
}
