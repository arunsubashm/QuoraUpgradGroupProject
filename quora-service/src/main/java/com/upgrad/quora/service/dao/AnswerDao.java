package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

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
}
