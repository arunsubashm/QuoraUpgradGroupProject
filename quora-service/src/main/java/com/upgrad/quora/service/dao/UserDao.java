package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * This method persist the user in db
     *
     * @param userEntity
     * @return UserEntity after successful insertion
     */
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * This method gets the user details by email
     *
     * @param email
     * @return UserEntity when email is found.
     * @throws NoResultException if no user details are found with email.
     */
    public UserEntity fetchUserByEmail(String email) {
        try {
            return entityManager.createNamedQuery("getUserByEmail", UserEntity.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    /**
     * This method gets the user details by userName
     *
     * @param userName
     * @return UserEntity when userName is found.
     * @throws NoResultException if no user details are found with userName.
     */
    public UserEntity fetchUserByUserName(String userName) {
        try {
            return entityManager.createNamedQuery("getUserByUserName", UserEntity.class)
                    .setParameter("username", userName).getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }
}

