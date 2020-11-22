package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
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
}
