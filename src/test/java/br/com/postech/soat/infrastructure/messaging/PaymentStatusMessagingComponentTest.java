package br.com.postech.soat.infrastructure.messaging;

import br.com.postech.soat.commons.application.Pagination;
import br.com.postech.soat.domain.valueobject.CustomerId;
import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderStatus;
import br.com.postech.soat.domain.valueobject.OrderId;
import br.com.postech.soat.infrastructure.messaging.dto.PaymentProcessedMessage;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentStatusMessagingComponentTest {

    @Test
    void shouldUpdateOrderStatusToPaidWhenPaymentFinishes() {
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        PaymentStatusListener listener = new PaymentStatusListener(orderRepository);

        UUID orderIdValue = UUID.randomUUID();
        Order order = new Order(
                new OrderId(orderIdValue),
                new CustomerId(UUID.randomUUID()),
                OrderStatus.AWAITING_PAYMENT,
                BigDecimal.TEN,
                BigDecimal.ZERO,
                List.of(),
                List.of());
        orderRepository.save(order);

        PaymentProcessedMessage message = new PaymentProcessedMessage(UUID.randomUUID(), orderIdValue, "FINISHED");

        listener.receive(message);

        Optional<Order> updated = orderRepository.findById(new OrderId(orderIdValue));
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldUpdateOrderStatusToAwaitingWhenPaymentFails() {
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        PaymentStatusListener listener = new PaymentStatusListener(orderRepository);

        UUID orderIdValue = UUID.randomUUID();
        Order order = new Order(
                new OrderId(orderIdValue),
                new CustomerId(UUID.randomUUID()),
                OrderStatus.IN_PREPARATION,
                BigDecimal.TEN,
                BigDecimal.ZERO,
                List.of(),
                List.of());
        orderRepository.save(order);

        PaymentProcessedMessage message = new PaymentProcessedMessage(UUID.randomUUID(), orderIdValue, "FAILED");

        listener.receive(message);

        Optional<Order> updated = orderRepository.findById(new OrderId(orderIdValue));
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(OrderStatus.AWAITING_PAYMENT);
    }

    static class InMemoryOrderRepository implements OrderRepository {
        private final Map<UUID, Order> storage = new ConcurrentHashMap<>();

        @Override
        public Order save(Order order) {
            storage.put(order.getId().getValue(), order);
            return order;
        }

        @Override
        public List<Order> findActiveOrdersSorted(Set<OrderStatus> orderStatuses, Pagination pagination) {
            return List.of();
        }

        @Override
        public Optional<Order> findById(OrderId orderId) {
            return Optional.ofNullable(storage.get(orderId.getValue()));
        }

        @Override
        public Order updateStatus(Order order) {
            Order existing = storage.get(order.getId());

            if (existing == null) {
                throw new IllegalStateException("Order not found");
            }

            existing.setStatus(order.getStatus());
            return existing;
        }
    }
}
