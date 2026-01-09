package br.com.postech.soat.infrastructure.gateways;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.postech.soat.commons.application.gateways.ProductGateway;
import br.com.postech.soat.commons.application.gateways.ProductDto;

import br.com.postech.soat.infrastructure.integration.ProductClient;
import br.com.postech.soat.infrastructure.integration.ProductResponseDto;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductGatewayImpl implements ProductGateway {
    private final ProductClient productClient;

    @Override
    public Optional<ProductDto> findProduct(UUID id) {
        try {
            ProductResponseDto response = productClient.findById(id);
            
            // Conversão: Infra DTO -> Domain DTO
            return Optional.of(new ProductDto(
                response.id(),
                response.name(),
                response.price(),
                response.category()
            ));
        } catch (FeignException.NotFound e) {
            return Optional.empty(); // Retorna vazio se o produto não existir
        }
    }
}
