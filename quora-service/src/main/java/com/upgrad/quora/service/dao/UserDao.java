package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

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

    /**
     * This method gets the Authentication access token of the user from database
     *
     * @param accessToken
     * @return UserEntity when userName is found.
     * @throws NoResultException if there is no accesstoken generated for the user.
     */
    public UserAuthTokenEntity getUserByAuthtoken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public void updateUser(UserEntity updateUserEntity) {
        entityManager.merge(updateUserEntity);
    }

    /**This method deletes the user and flush's it.
     *
     * @param userId UUID to be deleted
     * @return true
     */
    public boolean deleteUser(String userId) {
        entityManager.createNamedQuery("deleteUserByUserId", UserEntity.class).setParameter("uuid", userId).executeUpdate();
        entityManager.flush();
        return true;
    }

    /** method to fetch UserEntity by uuid
     *
     * @param uuid Unique id of the user
     * @return UserEntity
     */
    public UserEntity getUserEntityById(final String uuid) {
        try {
            return entityManager.createNamedQuery("fetchUserByUserId", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}

