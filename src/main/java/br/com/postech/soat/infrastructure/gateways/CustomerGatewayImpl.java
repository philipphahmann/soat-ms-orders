package br.com.postech.soat.infrastructure.gateways;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.postech.soat.commons.application.gateways.CustomerGateway;
import br.com.postech.soat.commons.application.gateways.CustomerDto;

import br.com.postech.soat.infrastructure.integration.CustomerClient;
import br.com.postech.soat.infrastructure.integration.CustomerResponseDto;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerGatewayImpl implements CustomerGateway {
    private final CustomerClient customerClient; 

    @Override
    public Optional<CustomerDto> findCustomer(String cpf) {
        try {
            CustomerResponseDto response = customerClient.findByCpf(cpf);
            
            return Optional.of(new CustomerDto(
                UUID.fromString(response.id()),
                response.name(),
                response.cpf(),
                response.email()
            ));
        } catch (FeignException.NotFound e) {
            return Optional.empty(); 
        }
    }
}
