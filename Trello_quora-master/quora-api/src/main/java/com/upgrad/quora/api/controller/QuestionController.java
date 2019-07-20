package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

//This is Question Controller

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // Following Method Maps Functionality to creat Question Create
    // Endpoint - /question/create
    // Request - Questiondetails and User Authorization
    // Response - QuestionEntity
    // HttpStatus - Created
    // Exceptions - Authorization Failed Exception
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization,
                                                           final QuestionRequest questionRequest)
            throws AuthorizationFailedException {

        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        final QuestionEntity createQuestion = questionService.createQuestion(questionEntity, authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    // Following Method Maps Functionality to get All Questions
    // Endpoint - /question/all
    // Request - User Authorization
    // Response - List of Questions
    // HttpStatus - Ok
    // Exceptions - Authorization Failed Exception
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        List<QuestionEntity> questionEntities = questionService.getAllQuestions(authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<>();
        for (QuestionEntity questionEntity : questionEntities) {

            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid())
                    .content(questionEntity.getContent());
            questionDetailsResponseList.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);
    }

    // Following Method Maps Functionality to Edit Question
    // Endpoint - /question/edit/{questionId}
    // Request - Question Id / User Authorization
    // Response - Question
    // HttpStatus - Ok
    // Exceptions - Authorization Failed Exception and Invalid Question
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@PathVariable("questionId") final String questionId,
                                                             @RequestHeader("authorization") final String authorization,
                                                             final QuestionRequest questionEditRequest)
            throws  AuthorizationFailedException, InvalidQuestionException {

        final QuestionEntity editQuestionEntity = new QuestionEntity();
        editQuestionEntity.setContent(questionEditRequest.getContent());
        editQuestionEntity.setDate(ZonedDateTime.now());
        final QuestionEntity editedQuestion = questionService.updateQuestionEntity(editQuestionEntity, questionId, authorization);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    // Following Method Maps Functionality to Delete Question
    // Endpoint - /question/delete/{questionId}
    // Request - Question Id / User Authorization
    // Response - Deleted Question
    // HttpStatus - Ok
    // Exceptions - Authorization Failed Exception and Invalid Question
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId,
                                                                 @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        final String deletedQuestionEntityUuid = questionService.deleteQuestionEntity(questionId, authorization);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(deletedQuestionEntityUuid).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    // Following Method Maps Functionality to get Question list for user
    // Endpoint - uestion/all/{userId}
    // Request - User Id / User Authorization
    // Response - Question List by User
    // HttpStatus - Ok
    // Exceptions - Authorization Failed Exception and UserNotFound Exception
    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@PathVariable("userId") final String userId,
                                                                         @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {

        final List<QuestionEntity> questionEntities = questionService.getQuestionListByUser(userId, authorization);
        List<QuestionDetailsResponse> questionDetailsResponseList  = new ArrayList<>();
        for(QuestionEntity questionEntity:questionEntities) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid())
                    .content(questionEntity.getContent());
            questionDetailsResponseList.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);
    }
}