package br.com.postech.soat.commons.application.gateways;

import java.util.UUID;

public record CustomerDto(
    UUID id,
    String name,
    String cpf,
    String email
) {}