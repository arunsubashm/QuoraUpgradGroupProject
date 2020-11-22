package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {
    final String ROLE_NON_ADMIN = "nonadmin";

    @Autowired
    SignupBusinessService signupBusinessService;

    /**
     * This method persist the user if userName or email does
     *
     * @param signupUserRequest char array.
     * @return SignupUserResponse
     */
    @RequestMapping(method = RequestMethod.POST, path = "user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutme(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole(ROLE_NON_ADMIN);
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        UserEntity signup = signupBusinessService.signup(userEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse().id(signup.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(signupUserResponse, HttpStatus.CREATED);

    }
}

