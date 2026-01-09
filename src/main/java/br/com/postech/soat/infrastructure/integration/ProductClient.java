package br.com.postech.soat.infrastructure.integration;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${app.services.product-url}")
public interface ProductClient {    

    @GetMapping("/products/{id}")
    ProductResponseDto findById(@PathVariable("id") UUID id);
}
