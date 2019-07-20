package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    // Following method is used to persist new AnswerEntity in Database
    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    //Retrieve Answer form DB
    public AnswerEntity getAnswerByAnswerUuid(String answerUuid){
        try {
            AnswerEntity answerEntity = entityManager.createNamedQuery("getAnswerByAnswerUuid", AnswerEntity.class).setParameter("answerUuid", answerUuid).getSingleResult();
            return answerEntity;
        } catch (NoResultException nre){
            return null;
        }
    }

    //Edit the Answer and merge to the DB
    public AnswerEntity editAnsContents(final AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    //Delete the Answer form DB
    public AnswerEntity deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
        return answerEntity;
    }
}
