package br.com.postech.soat.application.usecases;

import br.com.postech.soat.application.command.UpdateOrderStatusCommand;
import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderStatus;
import br.com.postech.soat.domain.valueobject.OrderId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOrderStatusUseCaseTest {

    @InjectMocks
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void execute_ShouldUpdateStatus_WhenOrderExists() {
        // Cenário
        UUID orderUuid = UUID.randomUUID();
        OrderStatus newStatus = OrderStatus.IN_PREPARATION;
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderUuid, newStatus);

        Order existingOrder = mock(Order.class);
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(existingOrder));
        when(orderRepository.updateStatus(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Execução
        Order result = updateOrderStatusUseCase.execute(command);

        // Verificação
        verify(existingOrder).setStatus(newStatus);
        verify(orderRepository).updateStatus(existingOrder);
        Assertions.assertEquals(newStatus, OrderStatus.valueOf(command.getStatus().name()));
    }

    @Test
    void execute_ShouldThrowException_WhenOrderNotFound() {
        // Cenário
        UUID orderUuid = UUID.randomUUID();
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderUuid, OrderStatus.DONE);

        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.empty());

        // Execução & Verificação
        Assertions.assertThrows(IllegalArgumentException.class, () -> 
            updateOrderStatusUseCase.execute(command)
        );
        
        verify(orderRepository, never()).updateStatus(any());
    }
}