package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;


    /**
     * This method persist the user if userName or email is UNIQUE else throws SignUpRestrictedException
     *
     * @param userEntity
     * @return userEntity once it's persisted in db
     * @throws  SignUpRestrictedException if username and email already exits
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        //check if email is already registered by fetching the userEntity using username from the request
        final UserEntity userEntityEmail = userDao.fetchUserByEmail(userEntity.getEmail());
        //check if username is already registered by fetching the userEntity using username from the request
        final UserEntity userEntityUserName = userDao.fetchUserByUserName(userEntity.getUserName());
        if (null != userEntityUserName) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        if (null != userEntityEmail) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        // Encrypting the password
        String[] encrypt = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        //updating the request with encrypted password and the salt
        userEntity.setSalt(encrypt[0]);
        userEntity.setPassword(encrypt[1]);
        return userDao.createUser(userEntity);
    }

}
