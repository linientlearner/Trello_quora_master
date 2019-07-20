package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.LinkedList;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    //Method to create Answer response to the Question
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable(value = "questionId") final String questionUuid, @RequestHeader(value = "authorization") final String authorization) throws InvalidQuestionException, AuthorizationFailedException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());

        AnswerEntity createdAnswer = answerService.createAnswer(answerEntity, questionUuid, authorization);

        final AnswerResponse createdAnswerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(createdAnswerResponse, HttpStatus.CREATED);
    }

    //Method to Edit Answer
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @PathVariable(value = "answerId") final String answerId, @RequestHeader(value = "authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity ansEditEntity = new AnswerEntity();
        ansEditEntity.setUuid(UUID.randomUUID().toString());
        ansEditEntity.setAns(answerEditRequest.getContent());
        ansEditEntity.setDate(ZonedDateTime.now());

        AnswerEntity ansEdited = answerService.editAnswerContents(ansEditEntity, answerId, authorization);

        final AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(ansEdited.getUuid()).status("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    //Method to Delete Answer
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable(value = "answerId") final String answerId, @RequestHeader("authorization") final String authorization)throws AuthorizationFailedException, AnswerNotFoundException {
        final AnswerEntity deletedAnswer = answerService.deleteAnswer(answerId, authorization);
        AnswerDeleteResponse deletedAnsResponse = new AnswerDeleteResponse().id(deletedAnswer.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(deletedAnsResponse, HttpStatus.OK);
    }

    //Method to get all Answers to a Question
    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswerToQuestion(@PathVariable(value = "questionId") final String questionUuid, @RequestHeader(value = "authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        List<AnswerEntity> answerEntities = answerService.getAllAnswerToQuestion(questionUuid, authorization);
        List<AnswerDetailsResponse> answerDetailsResponsesList = new LinkedList<AnswerDetailsResponse>();

        for (AnswerEntity answerEntity : answerEntities) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerEntity.getUuid()).questionContent(answerEntity.getQuestion().getContent()).answerContent(answerEntity.getAns());
            answerDetailsResponsesList.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponsesList, HttpStatus.OK);
    }
}
