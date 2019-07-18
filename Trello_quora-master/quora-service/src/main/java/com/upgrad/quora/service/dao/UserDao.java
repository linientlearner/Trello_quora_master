package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    //Method fetches User Details from the Database by using userUuid that was passed
    public UsersEntity getUser(String userUuid){
        try {
            return entityManager.createNamedQuery("userByUuid", UsersEntity.class).setParameter("userUuid", userUuid).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
}
