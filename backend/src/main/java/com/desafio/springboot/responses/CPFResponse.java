package com.desafio.springboot.responses;

import lombok.Data;

@Data
public class CPFResponse {

  // Retorna se o usuário pode votar ou não, a partir do cpf do mesmo.
  private String status;

}