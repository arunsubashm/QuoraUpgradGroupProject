package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to get all Questions as List.
     * @param null.
     *
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
     * @param id.
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
    public QuestionEntity getSingleQuestionByUser (final String userID, final String questionId) {
        try {
            return entityManager.createNamedQuery("getSingleQuestionByUser", QuestionEntity.class)
                    .setParameter("uuid", userID)
                    .setParameter("id",questionId)
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
    public void deleteQuestionByUser (final int questionId) {
        entityManager.createQuery("delete from QuestionEntity u where u.id =:questionId").setParameter("questionId", questionId).executeUpdate();
    }
}
