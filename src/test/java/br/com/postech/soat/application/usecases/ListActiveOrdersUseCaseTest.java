package br.com.postech.soat.application.usecases;

import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.commons.application.Pagination;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListActiveOrdersUseCaseTest {

    @InjectMocks
    private ListActiveOrdersUseCase listActiveOrdersUseCase;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void shouldReturnActiveOrders() {
        // Cenário
        Pagination pagination = new Pagination(0, 10);
        List<Order> expectedOrders = List.of(mock(Order.class));
        
        when(orderRepository.findActiveOrdersSorted(any(), eq(pagination)))
                .thenReturn(expectedOrders);

        // Execução
        List<Order> result = listActiveOrdersUseCase.execute(pagination);

        // Verificação
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        
        // Garante que buscou pelos status corretos (Active List)
        verify(orderRepository).findActiveOrdersSorted(
            eq(OrderStatus.activeOrderStatusList()), 
            eq(pagination)
        );
    }
}