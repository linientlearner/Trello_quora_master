package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {

        return questionDao.createQuestion(questionEntity);
    }

    //following method returns List of all Questions
    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    //following method takes the Question uuid and user auth token and enables only owners to edit question
    public QuestionEntity getQuestion(final String questionUuId, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorization);

        if (questionDao.getQuestionByUuid(questionUuId) == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuId);

        if (questionEntity.getUser() == userAuthEntity.getUser()){
            return questionEntity;
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
    }

    //following method updates the existing Question
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestionEntity(final QuestionEntity questionEntity) {
        return questionDao.updateQuestionEntity(questionEntity);
    }

    //following method deletes the Question
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestionEntity(final QuestionEntity deleteQuestionEntity) {
        questionDao.deleteQuestionEntity(deleteQuestionEntity);
    }
}