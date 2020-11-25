package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
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

    public String deleteQuestion(final String accessToken, final String questionId) throws InvalidQuestionException, AuthorizationFailedException {

        // get User Entity details based on the accessToken
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(accessToken);

        // Check If the access token provided by the user does not exist in the database.
        // throw 'AuthorizationFailedException' with the message code - 'ATHR-001' and message - 'User has not signed in'

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check If the user has signed out,
        // throw 'AuthorizationFailedException' with the message code- 'ATHR-002' and message -'User is signed out.Sign in first to delete a question'.
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        QuestionEntity questionEntity = questionDao.getSingleQuestionByUser(userAuthTokenEntity.getUuid(), questionId);
        // Check If the question with uuid which is to be deleted exist in the database, other wise
        // throw 'InvalidQuestionException'  with the message code-'QUES-001' and message-'Entered question uuid does not exist'.

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        UserEntity userEntity = userDao.getUserEntityById(userAuthTokenEntity.getUuid());

         //if the user who is not the owner of the question or the role of the user is ‘nonadmin’ and
        // tries to delete the question, otherwise  throw 'AuthorizationFailedException' with message code-'ATHR-003'
        // and message -'Only the question owner or admin can delete the question'.

        if (!(userEntity.getUuid().equals(questionEntity.getUuid())) && !(userEntity.getRole().equals("admin"))) {

            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        // here deleting the question by question Id field is relevant as it is unique identifier column.
        // a user may have multiple questions so deleting by uuid field may delete multiple questions.

        questionDao.deleteQuestionByUser(questionEntity.getId());
        return questionEntity.getUuid();
    }
}
