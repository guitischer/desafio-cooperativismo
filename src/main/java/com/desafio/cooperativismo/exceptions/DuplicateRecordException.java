package com.desafio.cooperativismo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.desafio.cooperativismo.enums.ErrorMessageEnum;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateRecordException extends RuntimeException {

  private String message;

  public DuplicateRecordException(ErrorMessageEnum message) {
    this.message = message.getMessage();
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(ErrorMessageEnum message) {
    this.message = message.getMessage();
  }
}