package br.com.postech.soat.infrastructure.integration;

public record CustomerResponseDto(
    String id, 
    String name, 
    String cpf, 
    String email
) {}
