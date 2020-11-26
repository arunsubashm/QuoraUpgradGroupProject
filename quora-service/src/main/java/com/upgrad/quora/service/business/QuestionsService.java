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
    private UserDao userDao;


    @Transactional
    public List<QuestionEntity> getAllQuestions(final String accessToken) throws  AuthorizationFailedException  {
        UserAuthTokenEntity authTokenEntity = userDao.getUserByAuthtoken(accessToken);

        if (authTokenEntity == null) {
            throw  new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (authTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        List<QuestionEntity> questions = questionDao.getAllQuestions();

        return questions;
    }

    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity authTokenEntity = userDao.getUserByAuthtoken(accessToken);

         // Check If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException''

        if (authTokenEntity == null) {
            throw  new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check If the user has signed out, if yes throw 'AuthorizationFailedException' with the message code-'ATHR-002'
        // and message-'User is signed out.Sign in first to get all questions posted by a specific user'

        if (authTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

         // Check if the user with uuid whose questions are to be retrieved from the database does not exist in the database, throw 'UserNotFoundException''

        if (userDao.getUserEntityById(userId) == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        List<QuestionEntity> questionsList = questionDao.getAllQuestionsByUser(userId);

        return questionsList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
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

        QuestionEntity questionEntity = questionDao.getQuestionByUser(questionId);
        // Check If the question with uuid which is to be deleted exist in the database, other wise
        // throw 'InvalidQuestionException'  with the message code-'QUES-001' and message-'Entered question uuid does not exist'.

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        UserEntity userEntity = userDao.getUserEntityById(userAuthTokenEntity.getUuid());
        UserEntity uuidFromQuestionEntity = questionEntity.getUser();
         //if the user who is not the owner of the question or the role of the user is ‘nonadmin’ and
        // tries to delete the question, otherwise  throw 'AuthorizationFailedException' with message code-'ATHR-003'
        // and message -'Only the question owner or admin can delete the question'.
        if (!(userEntity.getUuid().equals(uuidFromQuestionEntity.getUuid())) && !(userEntity.getRole().equals("admin"))) {

            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        //  deleting the Question by uuid.

        questionDao.deleteQuestionByUser(questionEntity.getUuid());
        return questionEntity.getUuid();
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
        UserAuthTokenEntity authTokenEntity = userDao.getUserByAuthtoken(accessToken);

        // Throws custom exception when content(i.e question ) us empty
        if (null == questionEntity.getContent()){
            throw new RequestViolationException("IAE_001", "content cant be empty");
        }

        // Check If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException''
        if (authTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (authTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }
        //save the question
        questionEntity.setUser(authTokenEntity.getUser());
        return questionDao.saveQuestion(questionEntity);
    }
}
