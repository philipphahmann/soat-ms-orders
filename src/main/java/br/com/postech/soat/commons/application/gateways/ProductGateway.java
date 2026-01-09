package br.com.postech.soat.commons.application.gateways;

import java.util.Optional;
import java.util.UUID;

public interface ProductGateway {
    Optional<ProductDto> findProduct(UUID id);
}
