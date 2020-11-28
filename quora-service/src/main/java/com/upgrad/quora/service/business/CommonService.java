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

    @Autowired
    private AuthenticationService authenticationService;

    public UserEntity getUserDetails(final String userId, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = userDao.getUserById(userId);

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        UserAuthTokenEntity authTokenEntity = authenticationService.getAuthToken(accessToken, "User has not signed in",
                "User is signed out.Sign in first to get user details");

        return userEntity;
    }
}
