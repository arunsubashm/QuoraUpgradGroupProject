package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * Authentication class defines the Business logic for user SignIn Feature, and throws defined exceptions.
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * Method Auth is used for user authentication/ sign in purpose.
     * The user authenticates in the application and after successful authentication, JWT token is given to a user
     */

    public UserAuthTokenEntity auth(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.fetchUserByUserName(username);

        /**
         * Check If the username provided by the user exists . Otherwise throw exception AuthenticationFailedException ATH-001, "This username does not exist"
         */
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, userEntity.getSalt());

        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
            userAuthTokenEntity.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthTokenEntity.setUuid(userEntity.getUuid());
            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);

            // Create an authorization token and update the token.
            userDao.createAuthToken(userAuthTokenEntity);

            userDao.updateUser(userEntity);
            return userAuthTokenEntity;
        }
        /**
         * Check If the password provided by the match with the password in the existing database. If it does not match
         * Throw exception ATH-002, "Password failed"
         */
        else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }

    /**
     *
     * @param authToken authToken that is provided during login.
     * @param userId UUID of the user.
     * @return UUID of deleted user.
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String checkUserByAuthToken(String authToken, String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserByAuthtoken(authToken);
        UserEntity userEntity = userDao.getUserEntityById(userId);
        //if user is not registered in the system userEntity will be null
        if (null == userEntity){
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }
        // non-admin user cannot delete the users
        if (!"admin".equalsIgnoreCase(userEntity.getRole())){
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }
        //userAuthTokenEntity is null when user is registered successfully but not signed in to the system.
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //logoutAt() gets updated once the user is logged out
        if (null != userAuthTokenEntity.getLogoutAt()){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }
        // delete the user by UUID
        userDao.deleteUser(userId);
        return userEntity.getUuid();
    }
}
