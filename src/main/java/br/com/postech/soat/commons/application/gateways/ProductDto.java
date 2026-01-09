package br.com.postech.soat.commons.application.gateways;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
    UUID id,
    String name,
    BigDecimal price,
    String category
) {}