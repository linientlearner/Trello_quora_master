package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

// This is Service Class of QuestionController
@Service
public class QuestionService {

    //Coubling Service class with Question Respository to Create/Update/Get/Delete Question in Database
    @Autowired
    private QuestionDao questionDao;

    //Coubling Service class with User Authentication Repository to authenticate User in various functionalities
    @Autowired
    private UserAuthDao userAuthDao;

    // Coupling Service class with User Repository
    @Autowired
    private UserDao userDao;

    // Method Functionality - Following Method Creates and Returns Question
    // Method Input -> User Access Token and Question Entity
    // Validations - It Checks based on Access Token if User is Signed, if Yes It check if user has logged out
    // Exceptions - Both these checks are handled with Authorization failed Exception
    // If both above checks are ok - Then follwing Method creates Question Entity using Question Dao and returns it
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to post a question");
        }
        UsersEntity usersEntity = userAuthEntity.getUser();
        questionEntity.setUser(usersEntity);
        return questionDao.createQuestion(questionEntity);
    }

    // Method Functionality - Following Method Returns List of all Questions in the Repository
    // Method Input -> User Access Token
    // Validations - It Checks based on Access Token if User is Signed, if Yes It check if user has logged out
    // Exceptions - Both these checks are handled with Authorization failed Exception
    // If both above checks are ok - it usase Question Dao to get All Questions in the Repository
    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get all questions");
        }
        return questionDao.getAllQuestions();
    }

    // Method Functionality - Following Method Edits/Updates Question
    // Method Input -> Question Uuid and User Access Token and Question
    // Validations - It Checks based on Access Token if User is Signed, if Yes It check if user has logged out
    //               It validates if Access Token User and Question Owner are same
    //               It validates if Input Question Uuid exists in database
    // Exceptions - Above checks are handled with Authorization failed Exception and Invalid Question Exception
    // If Checks validations found ok - Method updates Existing Question with new details
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestionEntity(final QuestionEntity questionEntity, final String questionUuid,
                                               final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorization);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        QuestionEntity questionForEdit = questionDao.getQuestionByUuid(questionUuid);
        UsersEntity usersEntity = userAuthEntity.getUser();

        if (questionForEdit == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (questionForEdit.getUser() != userAuthEntity.getUser()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        questionForEdit.setUser(usersEntity);
        questionForEdit.setContent(questionEntity.getContent());
        questionForEdit.setDate(questionEntity.getDate());
        return questionDao.updateQuestionEntity(questionForEdit);
    }

    // Method Functionality - Following Method Deletes Question
    // Method Input -> Question Uuid and User Access Token and Question
    // Validations - It Checks based on Access Token if User is Signed, if Yes It check if user has logged out
    //               It validates if Access Token User and Question Owner are same or User has Role "Admin"
    //               It validates if Input Question Uuid exists in database
    // Exceptions - Above checks are handled with Authorization failed Exception and Invalid Question Exception
    // If Checks validations found ok - Method Deletes Existing Question and returns Uuid of Deleted Question
    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestionEntity(final String questionId, final String authorization)
                                        throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorization);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }
        QuestionEntity questionToBeDeleted = questionDao.getQuestionByUuid(questionId);

        if (questionToBeDeleted == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        UsersEntity questionUser = userAuthEntity.getUser();
        String role = questionUser.getRole();

        String deletedQuestionUuid = questionToBeDeleted.getUuid();
        if (questionUser == questionToBeDeleted.getUser() || role.equals("admin")) {
            questionDao.deleteQuestionEntity(questionToBeDeleted);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        return deletedQuestionUuid;
    }

    // Method Functionality - Following Method returns Question List for a given User
    // Method Input -> User Uuid and User Access Token
    // Validations - It Checks based on Access Token if User is Signed, if Yes It check if user has logged out
    //               It validates if Enter User Uuid exist in the database
    // Exceptions - Above checks are handled with Authorization failed Exception and User Not found Exception
    // If Checks validations found ok - Method returns List of Question for a given user
    public List<QuestionEntity> getQuestionListByUser(final String userUuid, final String authorization) throws AuthorizationFailedException,UserNotFoundException {

        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorization);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        if (userDao.getUser(userUuid) == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        } else {

            UsersEntity usersEntity = userDao.getUser(userUuid);
            return questionDao.getQuestionListByUser(usersEntity);
        }
    }
}