package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AnswerService {
    @Autowired
    AnswerDao answerDao;

    @Autowired
    UserAuthDao userAuthDao;

    @Autowired
    QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity, final String questionUuid, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        //Check if User is signed in
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to post an answer.");
        }

        //Check if the question entered is invalid
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid.");
        }

        answerEntity.setUser(userAuthEntity.getUser());
        answerEntity.setQuestion(questionEntity);
        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContents(AnswerEntity ansEditEntity, String answerId, String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorization);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");

        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to edit an answer.");
        }

        AnswerEntity answerEntity = answerDao.getAnswerByAnswerUuid(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist.");
        } else if (userAuthEntity.getUser() != answerEntity.getUser()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer.");
        } else {
            answerEntity.setAns(ansEditEntity.getAns());
            answerEntity.setDate(ZonedDateTime.now());
            return answerDao.editAnsContents(answerEntity);
        }
    }
}
