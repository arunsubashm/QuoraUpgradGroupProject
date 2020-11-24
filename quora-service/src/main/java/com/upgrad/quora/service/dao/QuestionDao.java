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
                    .setParameter("userId", userId)
                    .getResultList();

            return questionsList;
        } catch (NoResultException nre) {
          return null;
        }
    }
}
