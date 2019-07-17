package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    UserAuthDao userAuthDao;

    @Autowired
    PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UsersEntity createUser(UsersEntity usersEntity) throws SignUpRestrictedException {
        if (userDao.getUserByUsername(usersEntity.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        } else if (userDao.getUserByEmail(usersEntity.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        } else {
            String[] encryptedPassword = cryptographyProvider.encrypt(usersEntity.getPassword());
            usersEntity.setSalt(encryptedPassword[0]);
            usersEntity.setPassword(encryptedPassword[1]);
            userDao.createUser(usersEntity);
            return usersEntity;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticateUser(String username, String password) throws AuthenticationFailedException {
        UsersEntity usersEntity = userDao.getUserByUsername(username);
        if (usersEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        String encryptedPassword = cryptographyProvider.encrypt(password, usersEntity.getSalt());
        if(encryptedPassword.equals(usersEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthEntity= new UserAuthEntity();
            userAuthEntity.setUser(usersEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiredAt = now.plusHours(8);

            userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(usersEntity.getUuid(), now, expiredAt));
            userAuthEntity.setLoginAt(now);
            userAuthEntity.setExpiresAt(expiredAt);
            userAuthEntity.setUuid(UUID.randomUUID().toString());
            userAuthDao.createAuthToken(userAuthEntity);

            return userAuthEntity;
        }
        else{
            throw new AuthenticationFailedException("ATH--002", "Password failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signOut(String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        userAuthEntity.setLogoutAt(ZonedDateTime.now());
        return userAuthDao.logOut(userAuthEntity);
    }

}