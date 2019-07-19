package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest)
                                                            throws AuthorizationFailedException {

    final UserAuthEntity userAuthEntity = commonBusinessService.validateAuthToken(authorization);

        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUser(userAuthEntity.getUser());
        final QuestionEntity createdQuestion = questionService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        final UserAuthEntity userAuthTokenEntity = commonBusinessService.getAllValidateAuthToken(authorization);

        final List<QuestionEntity> questionEntities = questionService.getAllQuestions();

        List<QuestionResponse> questionResponse = new ArrayList<QuestionResponse>();
        QuestionResponse questionSingleResponse = new QuestionResponse();

        for (int i = 0; i < questionEntities.size(); i++) {
            questionResponse.add(questionSingleResponse.id(questionEntities.get(i).getUuid())
                    .status(questionEntities.get(i).getContent()));
        }
        return new ResponseEntity<List<QuestionResponse>>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> editQuestion(@PathVariable("questionId") final String questionId,
                                                         @RequestHeader("authorization") final String authorization,
                                                         final QuestionRequest questionRequest)
            throws AuthenticationFailedException, AuthorizationFailedException, InvalidQuestionException {

        final UserAuthEntity userAuthEntity = commonBusinessService.editValidateAuthToken(authorization);

        final QuestionEntity editedQuestionEntity = questionService.getQuestion(questionId, authorization);
        editedQuestionEntity.setContent(questionRequest.getContent());
        editedQuestionEntity.setDate(ZonedDateTime.now());
        editedQuestionEntity.setUser(userAuthEntity.getUser());

        final QuestionEntity editedQuestion = questionService.updateQuestionEntity(editedQuestionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> deleteQuestion(@PathVariable("questionId") final String questionId,
                                                           @RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException, AuthorizationFailedException, InvalidQuestionException {

        final UserAuthEntity userAuthEntity = commonBusinessService.deleteValidateAuthToken(authorization);

        final QuestionEntity deleteQuestionEntity = questionService.getQuestion(questionId, authorization);

        final String deletedQuestionUuid = deleteQuestionEntity.getUuid();

        questionService.deleteQuestionEntity(deleteQuestionEntity);

        QuestionResponse questionResponse = new QuestionResponse().id(deletedQuestionUuid).status("QUESTION DELETED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }
}
