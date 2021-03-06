package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {
    final String ROLE_NON_ADMIN = "nonadmin";

    @Autowired
    SignupBusinessService signupBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * This method expose end point user/signup, used to register new user to quora
     *
     * @param signupUserRequest request to register user.
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

    /**
     * This method expose end point user/signin
     *
     * @param authorization : password authorization.
     * @return SigninResponse
     */

    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        String[] splitText = authorization.split("Basic ");
        SigninResponse signinResponse = null;
        HttpHeaders headers = new HttpHeaders();
        byte[] decoder = Base64.getDecoder().decode(splitText[0]);
        String decodedText = new String(decoder);
        String[] decodedTextArray = decodedText.split(":");
        if (decodedTextArray.length == 2) {
            UserAuthTokenEntity userAuthToken = authenticationService.signin(decodedTextArray[0], decodedTextArray[1]);

            UserEntity user = userAuthToken.getUser();

            signinResponse = new SigninResponse().id(user.getUuid())
                    .message("SIGNED IN SUCCESSFULLY");


            headers.add("access-token", userAuthToken.getAccessToken());
            return new ResponseEntity<>(signinResponse, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * This method expose end point user/signout
     *
     * @param authorization : authorization token.
     * @return SignoutResponse
     */

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        UserEntity signout = authenticationService.signout(authorization);

        SignoutResponse SignoutResponse = new SignoutResponse().id(signout.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(SignoutResponse, HttpStatus.OK);

    }
}


