package br.com.postech.soat.infrastructure.gateways;

import br.com.postech.soat.commons.application.gateways.CustomerDto;
import br.com.postech.soat.commons.application.gateways.ProductDto;
import br.com.postech.soat.infrastructure.integration.CustomerClient;
import br.com.postech.soat.infrastructure.integration.CustomerResponseDto;
import br.com.postech.soat.infrastructure.integration.ProductClient;
import br.com.postech.soat.infrastructure.integration.ProductResponseDto;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GatewayTests {

    @InjectMocks private CustomerGatewayImpl customerGateway;
    @Mock private CustomerClient customerClient;

    @InjectMocks private ProductGatewayImpl productGateway;
    @Mock private ProductClient productClient;

    @Test
    void findCustomer_WhenExists_ShouldReturnDto() {
        String cpf = "123";
        CustomerResponseDto response = new CustomerResponseDto(UUID.randomUUID().toString(), "Name", cpf, "email");
        when(customerClient.findByCpf(cpf)).thenReturn(response);

        Optional<CustomerDto> result = customerGateway.findCustomer(cpf);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    void findCustomer_WhenNotFound_ShouldReturnEmpty() {
        when(customerClient.findByCpf("999")).thenThrow(FeignException.NotFound.class);
        Optional<CustomerDto> result = customerGateway.findCustomer("999");
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findProduct_WhenExists_ShouldReturnDto() {
        UUID id = UUID.randomUUID();
        ProductResponseDto response = new ProductResponseDto(id, "Name", BigDecimal.TEN, "CAT");
        when(productClient.findById(id)).thenReturn(response);

        Optional<ProductDto> result = productGateway.findProduct(id);
        Assertions.assertTrue(result.isPresent());
    }
}