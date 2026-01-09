package br.com.postech.soat.infrastructure.integration;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "customer-service", url = "${app.services.customer-url}")
public interface CustomerClient {
    
    @GetMapping("/customers")
    CustomerResponseDto findByCpf(@RequestParam("cpf") String cpf);
}
