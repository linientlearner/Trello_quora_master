package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthDao {
<<<<<<< HEAD

    @PersistenceContext
    private EntityManager entityManager;

    public UserAuthEntity getAuthToken(final String authorizationToken){
        try{
            UserAuthEntity userAuthEntity = entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class).setParameter("accessToken", authorizationToken).getSingleResult();
            return userAuthEntity;
        }
        catch (NoResultException nre){
=======
    @PersistenceContext
    private EntityManager entityManager;

    //Method receives the access token and checks if the user is logged in
    public UserAuthEntity getAuthToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch(NoResultException nre) {
>>>>>>> 00590ef3062d0929c544084faa727352a7c21fec
            return null;
        }
    }

<<<<<<< HEAD
    public UserAuthEntity createAuthToken(UserAuthEntity userAuthEntity){
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public UserAuthEntity logOut(UserAuthEntity userAuthEntity) {
        entityManager.merge(userAuthEntity);
        return userAuthEntity;
    }
=======
>>>>>>> 00590ef3062d0929c544084faa727352a7c21fec
}
