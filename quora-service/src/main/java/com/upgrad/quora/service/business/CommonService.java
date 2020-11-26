package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    @Autowired
    private UserDao userDao;

    public UserEntity getUserDetails(final String userId, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity authTokenEntity = userDao.getUserByAuthtoken(accessToken);

        if (authTokenEntity == null) {
            throw  new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (authTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        if (userId.equals(authTokenEntity.getUser().getUuid()) == false) {
            throw new AuthorizationFailedException("ATHR-003", "The passed UUID and the User of the authorization token does not match");

        }

        UserEntity userEntity = userDao.getUserEntityById(userId);

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return userEntity;
    }
}
