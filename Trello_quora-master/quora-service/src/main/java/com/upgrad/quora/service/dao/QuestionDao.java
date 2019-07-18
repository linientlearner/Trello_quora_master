package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Following method is used to persist new QuestionEntity in Database
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    // Following Query gives all the questions from Database
    public List<QuestionEntity> getAllQuestions(){
        try{
            return  entityManager.createNamedQuery("displayAllQuestions", QuestionEntity.class).getResultList();

        }
        catch (NoResultException nre){
            return  null;
        }
    }

    //Following Query method takes Question UUID as input and gives Question details out
    public QuestionEntity getQuestionByUuid(final String questionUuid){
        try{
            return  entityManager.createNamedQuery("questionByUuid", QuestionEntity.class)
                    .setParameter("uuid", questionUuid).getSingleResult();
        }
        catch (NoResultException nre){
            return  null;
        }
    }

    // Following mthod updates existing Question in database
    public QuestionEntity updateQuestionEntity(final QuestionEntity editedQuestionEntity) {
        try {
            return entityManager.merge(editedQuestionEntity);
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public void deleteQuestionEntity(final QuestionEntity deleteQuestionEntity) {
        try {
            entityManager.remove(deleteQuestionEntity);
        }
        catch(NoResultException nre){
        }
    }
}
