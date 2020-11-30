package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AuthenticationService authenticationService;

    /**Deletes The answer based on AnswerID
     *  @param authorization
     * @param answerId
     * @return AnswerEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteQuestion(String authorization, String answerId) throws AnswerNotFoundException, AuthorizationFailedException {

        final UserAuthTokenEntity userAuthDetails = authenticationService.getAuthToken(authorization, "User has not signed in", "User is signed out.Sign in first to delete an answer");
        AnswerEntity answerDetails = getAnswerDetailsWithAnswerId(answerId);
        //user should either be ADMIN or owner to delete the answer
        if (!userAuthDetails.getUser().getRole().equalsIgnoreCase("ADMIN")
                && !userAuthDetails.getUser().getUuid().equals(answerDetails.getUserId().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
        //delete answer by answerId
        answerDao.deleteAnswer(answerId);
        return answerDetails;
    }

    /** Creates a answer
     *  @param authorization
     * @param AnswerEntity
     * @return AnswerEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(String authorization, String questionId, AnswerEntity answerEntity) throws InvalidQuestionException, AuthorizationFailedException {

        final UserAuthTokenEntity userAuthDetails = authenticationService.getAuthToken(authorization, "User has not signed in", "User is signed out.Sign in first to post an answer");
        final QuestionEntity questionEntity = questionDao.getQuestionById(questionId);

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        answerEntity.setQuestionId(questionEntity);
        answerEntity.setUserId(userAuthDetails.getUser());

        return answerDao.saveAnswer(answerEntity);

    }


    /** check if there are any answer with provided answerId.
     *
     * @param answerId to fetch Answer Details from db
     * @return answerDetails details of Answer for provided answerId
     * @throws AnswerNotFoundException if answer is not found
     */
    private AnswerEntity getAnswerDetailsWithAnswerId(@NotNull String answerId) throws AnswerNotFoundException {
        AnswerEntity answerDetails = answerDao.getAnswerDetails(answerId);
        //if answerDetails are not found then throws AnswerNotFoundException
        if (answerDetails == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        return answerDetails;
    }

    /**
     *
     * @param authorization access-token
     * @param answerId to fetch Answer Details from db
     * @param answerEntity
     * @return answerDetails details of Answer for provided answerId
     * @throws AuthorizationFailedException if invalid access token is provided
     * @throws AnswerNotFoundException if answer is not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(String authorization, String answerId, AnswerEntity answerEntity) throws AuthorizationFailedException, AnswerNotFoundException {
        final UserAuthTokenEntity userAuthDetails = authenticationService.getAuthToken(authorization, "User has not signed in", "User is signed out.Sign in first to edit an answer");
        final AnswerEntity answerDetailsWithAnswerId = getAnswerDetailsWithAnswerId(answerId);

        //only the person who has posted the question has the ablity to edit it else throw AuthorizationFailedException
        if (!userAuthDetails.getUser().getUuid().equals(answerDetailsWithAnswerId.getUserId().getUuid())){
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerDetailsWithAnswerId.setAnswer(answerEntity.getAnswer());
        answerDetailsWithAnswerId.setDate(answerEntity.getDate());
        return answerDao.editAnswer(answerDetailsWithAnswerId);
    }
}
