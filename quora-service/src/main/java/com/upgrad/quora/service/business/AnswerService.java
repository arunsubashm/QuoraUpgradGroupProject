package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.RequestViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    @Autowired
    AnswerDao answerDao;

    /**Deletes The answer based on AnswerID
     *
     * @param answerId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(String answerId) {
        answerDao.deleteAnswer(answerId);
    }

    /** fetches the Answer details using answerID
     *
     * @param answerId
     * @return AnswerEntity
     * @throws AnswerNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity getAnswerDetailsByAnswerId(String answerId) throws AnswerNotFoundException {
        AnswerEntity answerDetails = answerDao.getAnswerDetails(answerId);
        //if answerDetails are not found then throws AnswerNotFoundException
        if (answerDetails == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        return answerDetails;
    }

    /** This methord is used to validate, Only the answer owner or admin can delete the answer
     * else throws  AuthorizationFailedException
     * @param userAuthDetails
     * @param answerEntity
     * @throws AuthorizationFailedException
     */
    public void validateTheRequestToDeleteAnswer(UserAuthTokenEntity userAuthDetails,
                                                 AnswerEntity answerEntity) throws AuthorizationFailedException {
        //user should either be ADMIN or owner to delete the answer
        if (!userAuthDetails.getUser().getRole().equalsIgnoreCase("ADMIN")
                && !userAuthDetails.getUser().getUuid().equals(answerEntity.getUserId().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
    }
}
