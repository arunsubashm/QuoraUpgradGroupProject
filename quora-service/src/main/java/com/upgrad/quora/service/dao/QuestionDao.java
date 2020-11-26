package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to get all Questions as List.
     * @return List<QuestionEntity>
     * @Catch Exception NoResultException
     */
    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to get all Questions by a User ID.
     * @param userId.
     *
     * @return List<QuestionEntity>
     * @Catch Exception NoResultException
     */
    public List<QuestionEntity> getAllQuestionsByUser(final String userId) {
        try {
            List questionsList = entityManager.createNamedQuery("getAllQuestionsByUser")
                    .setParameter("uuid", userId)
                    .getResultList();

            return questionsList;
        } catch (NoResultException nre) {
          return null;
        }
    }

    /**
     * Method to get a Questions by of a user by Question ID.
     * @param userID
     * @Param questionID
     * @return QuestionEntity
     * @Catch Exception NoResultException
     */
    public QuestionEntity getQuestionByUser (final String questionId) {
        try {
            return entityManager.createNamedQuery("getQuestionByUser", QuestionEntity.class)
                    .setParameter("uuid", questionId)
                    .getSingleResult();
        }
        catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to delete a Questions by Question ID.
     * @Param questionID
     * @return QuestionEntity
     * @Catch Exception NoResultException
     */
    public void deleteQuestionByUser (final String questionId) {
        Query query = entityManager.createQuery("DELETE from QuestionEntity q where q.uuid = :uuid");
        int deleteCount = query.setParameter("uuid", questionId).executeUpdate();

    }

    /** Method to save question
     * @param questionEntity
     * @return QuestionEntity
     */
    public QuestionEntity saveQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /** Method to update question
     * @param questionEntity
     * @return none
     */
    public void updateQuestion(final QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }
}
