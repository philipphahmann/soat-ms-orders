package br.com.postech.soat.commons.application.gateways;

import java.util.Optional;

public interface CustomerGateway {
    Optional<CustomerDto> findCustomer(String cpf);
}
