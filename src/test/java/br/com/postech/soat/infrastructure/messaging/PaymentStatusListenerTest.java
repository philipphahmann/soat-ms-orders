package br.com.postech.soat.infrastructure.messaging;

import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.valueobject.OrderId;
import br.com.postech.soat.infrastructure.messaging.dto.PaymentProcessedMessage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentStatusListenerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentStatusListener listener;

    private UUID orderUuid;

    @BeforeEach
    void setUp() {
        orderUuid = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should mark order as paid when payment is finished")
    void shouldMarkOrderAsPaid() {
        PaymentProcessedMessage message = new PaymentProcessedMessage(UUID.randomUUID(), orderUuid, "FINISHED");
        Order order = mock(Order.class);
        doReturn(new OrderId(orderUuid)).when(order).getId();
        doReturn(Optional.of(order)).when(orderRepository).findById(any(OrderId.class));

        listener.receive(message);

        verify(order).markPaid();
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should mark order as awaiting payment when payment fails")
    void shouldMarkOrderAsAwaitingPayment() {
        PaymentProcessedMessage message = new PaymentProcessedMessage(UUID.randomUUID(), orderUuid, "FAILED");
        Order order = mock(Order.class);
        doReturn(new OrderId(orderUuid)).when(order).getId();
        doReturn(Optional.of(order)).when(orderRepository).findById(any(OrderId.class));

        listener.receive(message);

        verify(order).markAwaitingPayment();
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should throw when order does not exist")
    void shouldThrowWhenOrderNotFound() {
        PaymentProcessedMessage message = new PaymentProcessedMessage(UUID.randomUUID(), orderUuid, "FINISHED");
        doReturn(Optional.empty()).when(orderRepository).findById(any(OrderId.class));

        assertThrows(IllegalArgumentException.class, () -> listener.receive(message));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when payment status is unknown")
    void shouldThrowForUnknownStatus() {
        PaymentProcessedMessage message = new PaymentProcessedMessage(UUID.randomUUID(), orderUuid, "UNKNOWN");

        assertThrows(IllegalArgumentException.class, () -> listener.receive(message));
        verify(orderRepository, never()).save(any());
    }
}
