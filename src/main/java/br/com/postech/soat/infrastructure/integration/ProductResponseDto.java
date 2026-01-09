package br.com.postech.soat.infrastructure.integration;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDto(UUID id, String name, BigDecimal price, String category) {}
