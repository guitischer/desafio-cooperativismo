package com.desafio.cooperativismo.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafio.cooperativismo.dtos.TopicDTO;
import com.desafio.cooperativismo.models.Topic;
import com.desafio.cooperativismo.services.TopicService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/topics")
@Api(value = "Endpoints de Pautas")
public class TopicController {

  @Autowired
  TopicService topicService;

  @ApiOperation(value = "Listagem das pautas")
  @GetMapping
  public ResponseEntity<List<Topic>> list() {
    List<Topic> topics = topicService.getTopics();
    return ResponseEntity.ok(topics);
  }

  @ApiOperation(value = "Criação de pauta")
  @PostMapping
  public ResponseEntity<Topic> create(@RequestBody @Valid TopicDTO topicDTO) {
    topicService.createTopic(topicDTO);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
