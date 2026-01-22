package br.com.postech.soat.infrastructure.http;

import br.com.postech.soat.application.command.CreateOrderCommand;
import br.com.postech.soat.application.command.UpdateOrderStatusCommand;
import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.application.usecases.CreateOrderUseCase;
import br.com.postech.soat.application.usecases.ListActiveOrdersUseCase;
import br.com.postech.soat.application.usecases.UpdateOrderStatusUseCase;
import br.com.postech.soat.openapi.model.PutOrdersRequestDto;
import br.com.postech.soat.commons.application.Pagination;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderStatus;
import br.com.postech.soat.domain.valueobject.CustomerId;
import br.com.postech.soat.domain.valueobject.OrderId;
import br.com.postech.soat.openapi.model.PostOrdersRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private OrderController orderController;

    @Mock
    private CreateOrderUseCase createOrderUseCase;
    
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @BeforeEach
    void setup() {
        orderController = new OrderController(createOrderUseCase, orderRepository);
        
        ListActiveOrdersUseCase listUseCaseMock = mock(ListActiveOrdersUseCase.class);
        ReflectionTestUtils.setField(orderController, "listActiveOrdersUseCase", listUseCaseMock);
        ReflectionTestUtils.setField(orderController, "updateOrderStatusUseCase", updateOrderStatusUseCase);
    }

    @Test
    void postOrders_ShouldReturnCreated() {
        // Cenário
        PostOrdersRequestDto request = new PostOrdersRequestDto();
        request.setCustomerId("12345678900");
        request.setItems(List.of()); 
        request.setNotes(List.of()); 
        
        Order orderMock = mock(Order.class);
        when(orderMock.getId()).thenReturn(new OrderId(UUID.randomUUID()));
        when(orderMock.getStatus()).thenReturn(OrderStatus.RECEIVED);
        when(orderMock.getTotalPrice()).thenReturn(BigDecimal.TEN);
        when(orderMock.getOriginalPrice()).thenReturn(BigDecimal.TEN);
        when(orderMock.getDiscountAmount()).thenReturn(BigDecimal.ZERO);

        when(createOrderUseCase.execute(any(CreateOrderCommand.class))).thenReturn(orderMock);

        // Execução
        var response = orderController.postOrders(request);

        // Verificação
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void getOrders_ShouldReturnOk_WhenOrdersExist() {
        ListActiveOrdersUseCase listUseCaseMock = mock(ListActiveOrdersUseCase.class);
        ReflectionTestUtils.setField(orderController, "listActiveOrdersUseCase", listUseCaseMock);

        Order orderMock = mock(Order.class);
        when(orderMock.getId()).thenReturn(new OrderId(UUID.randomUUID()));
        when(orderMock.getCustomerId()).thenReturn(new CustomerId(UUID.randomUUID()));
        when(orderMock.getStatus()).thenReturn(OrderStatus.IN_PREPARATION);
        when(orderMock.getTotalPrice()).thenReturn(BigDecimal.TEN);
        when(orderMock.getDiscountAmount()).thenReturn(BigDecimal.ZERO);

        when(listUseCaseMock.execute(any(Pagination.class))).thenReturn(List.of(orderMock));

        var response = orderController.getOrders(0, 10);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    void getOrders_ShouldReturnNoContent_WhenEmpty() {
        ListActiveOrdersUseCase listUseCaseMock = mock(ListActiveOrdersUseCase.class);
        ReflectionTestUtils.setField(orderController, "listActiveOrdersUseCase", listUseCaseMock);

        when(listUseCaseMock.execute(any(Pagination.class))).thenReturn(Collections.emptyList());

        var response = orderController.getOrders(0, 10);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void putOrders_ShouldReturnCreated() {
        // Cenário
        UUID orderId = UUID.randomUUID();
        PutOrdersRequestDto request = new PutOrdersRequestDto();
        request.setStatus("DONE");

        Order orderMock = mock(Order.class);
        when(orderMock.getStatus()).thenReturn(OrderStatus.DONE);

        when(updateOrderStatusUseCase.execute(any(UpdateOrderStatusCommand.class))).thenReturn(orderMock);

        // Execução
        var response = orderController.putOrders(orderId, request);

        // Verificação
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("DONE", response.getBody().getStatus());
    }
}