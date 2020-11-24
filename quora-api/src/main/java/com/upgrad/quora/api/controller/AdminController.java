package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    AuthenticationService authenticationService;

    /** This end-point is used to remove a user from Quora db
     *
     * @param authorization
     * @param userId UUID of the user who has to be deleted from db
     * @return UUID of the deleted user
     * @throws AuthorizationFailedException unAuthorization exception is thrown when non admin user tries to delete the User
     * @throws UserNotFoundException thrown when trying to delete un-registered user
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@RequestHeader("authorization") final String authorization, @PathVariable String userId) throws AuthorizationFailedException, UserNotFoundException {
        String deletedUserUUID = authenticationService.deleteUserByAuthToken(authorization, userId);
        UserDeleteResponse response = new UserDeleteResponse().id(deletedUserUUID).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(response, HttpStatus.OK);
    }
}
