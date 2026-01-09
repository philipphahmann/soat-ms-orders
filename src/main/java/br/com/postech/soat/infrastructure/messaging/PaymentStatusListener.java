package br.com.postech.soat.infrastructure.messaging;

import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderStatus;
import br.com.postech.soat.domain.valueobject.OrderId;
import br.com.postech.soat.infrastructure.messaging.dto.PaymentProcessedMessage;
import br.com.postech.soat.infrastructure.messaging.dto.PaymentStatusMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStatusListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStatusListener.class);

    private final OrderRepository orderRepository;

    @SqsListener("${app.messaging.payment-status-queue}")
    public void receive(PaymentProcessedMessage message) {
        LOGGER.info("Received payment status update for order {} with status {}", message.orderId(), message.status());

        PaymentStatusMessage paymentStatus = PaymentStatusMessage.from(message.status());
        OrderStatus targetStatus = paymentStatus.toOrderStatus();

        Order order = orderRepository.findById(new OrderId(message.orderId()))
                .orElseThrow(() -> new IllegalArgumentException("Order not found for id " + message.orderId()));

        switch (targetStatus) {
            case PAID -> order.markPaid();
            case AWAITING_PAYMENT -> order.markAwaitingPayment();
            default -> {
                LOGGER.warn("Unhandled order status mapping {} for order {}", targetStatus, message.orderId());
                return;
            }
        }

        orderRepository.save(order);
        LOGGER.info("Order {} updated to status {}", order.getId().getValue(), order.getStatus());
    }
}
