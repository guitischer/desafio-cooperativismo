package com.desafio.springboot.dtos;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class TopicDTO {

  @NotBlank
  private String name;

  private String description;
}
