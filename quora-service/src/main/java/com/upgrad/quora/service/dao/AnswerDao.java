package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /** Method to save answer
     * @param answerEntity
     * @return answerEntity
     */
    public AnswerEntity saveAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /** Method to update answer
     * @param answerEntity
     * @return none
     */
    public void updateQuestion(final AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
    }

    /** deletes the answer and return's true
     *
     * @param answerId
     * @return true
     */
    public boolean deleteAnswer(String answerId) {
        entityManager.createNamedQuery("deleteAnswerByAnswerId")
                .setParameter("uuid", answerId).executeUpdate();
        entityManager.flush();
        return true;
    }

    /** fetches all AnswerDetails for the answerId
     *
     * @param answerId UUID of the anser
     * @return AnswerEntity
     */
    public AnswerEntity getAnswerDetails(String answerId) {
        try {
           return entityManager.createNamedQuery("getAnswerDetailsByAnswerId", AnswerEntity.class)
                    .setParameter("uuid", answerId)
                    .getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    /** edit the answer details
     * @param answerEntity the details that needs to be updated
     * @return answerEntity once merge is success
     */
    public AnswerEntity editAnswer(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
        return answerEntity;
    }
}
