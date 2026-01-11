package br.com.postech.soat.application.usecases;

import br.com.postech.soat.application.command.CreateOrderCommand;
import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.commons.application.gateways.CustomerDto;
import br.com.postech.soat.commons.application.gateways.CustomerGateway;
import br.com.postech.soat.commons.application.gateways.ProductDto;
import br.com.postech.soat.commons.application.gateways.ProductGateway;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderItem;
import br.com.postech.soat.domain.valueobject.Discount;
import br.com.postech.soat.domain.valueobject.Observation;
import br.com.postech.soat.infrastructure.messaging.OrderPaymentPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderPaymentPublisher orderPaymentPublisher;
    @Mock
    private CustomerGateway customerGateway;
    @Mock
    private ProductGateway productGateway;

    @BeforeEach
    void setup() {
        // Injeta o valor da property @Value
        ReflectionTestUtils.setField(createOrderUseCase, "defaultPaymentMethod", "PIX");
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        // Cenario
        String cpf = "12345678900";
        UUID productId = UUID.randomUUID();
        
        // Mock do Cliente
        CustomerDto customerDto = new CustomerDto(UUID.randomUUID(), "João", cpf, "joao@email.com");
        when(customerGateway.findCustomer(cpf)).thenReturn(Optional.of(customerDto));

        // Mock do Produto
        ProductDto productDto = new ProductDto(
            productId, 
            "Lanche", 
            BigDecimal.valueOf(20.0), 
            "SNACK"
        );
        when(productGateway.findProduct(productId)).thenReturn(Optional.of(productDto));

        // Mock do Repository (salvar)
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Montagem do Comando (Entrada)
        // Nota: Precisamos criar um OrderItem com Discount NÃO NULO ou tratar no mapper, 
        // mas aqui estamos testando o UseCase, então passamos o objeto já instanciado.
        Discount discount = new Discount(BigDecimal.ZERO);
        OrderItem item = new OrderItem(productId, "Lanche Fake", 1, BigDecimal.TEN, "SNACK", discount);
        
        CreateOrderCommand command = new CreateOrderCommand(
                cpf,
                List.of(discount),
                List.of(item),
                List.of(new Observation("Sem cebola"))
        );

        // Execução
        Order result = createOrderUseCase.execute(command);

        // Verificações (Asserts)
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(BigDecimal.valueOf(20.0), result.getOriginalPrice()); // Preço veio do ProductGateway (20) e não do item (10)
        
        // Verifica se chamou as dependências
        verify(customerGateway).findCustomer(cpf);
        verify(productGateway).findProduct(productId);
        verify(orderRepository).save(any(Order.class));
        verify(orderPaymentPublisher).publish(any());
    }
}