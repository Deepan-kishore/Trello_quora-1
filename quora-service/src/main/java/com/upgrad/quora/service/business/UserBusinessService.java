package com.upgrad.quora.service.business;

import org.springframework.stereotype.Service;
import com.upgrad.quora.service.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.upgrad.quora.service.exception.*;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;


@Service
public class UserBusinessService {
    @Autowired
    private AdminBusinessService adminBusinessService;

    @Autowired
    private UserDao userDao;

    /**
     * This method helps to create a new user
     *
     * @param userEntity  from which new user will be created
     *
     * @return UserEntity object
     *
     * @throws SignUpRestrictedException if validation for user details has some issues
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        return adminBusinessService.createUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String authorizationToken) throws SignOutRestrictedException {
        return adminBusinessService.logoutUser(authorizationToken);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String userId, final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);

        // It checks weather user is logged in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // It checks weather an user is logged out or not
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        // It checks if an user exists or not
        UserEntity userEntity = userDao.getUserByUuid(userId);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return userEntity;
    }
}
