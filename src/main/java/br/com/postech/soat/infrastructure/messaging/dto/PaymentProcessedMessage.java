package br.com.postech.soat.infrastructure.messaging.dto;

import java.util.UUID;

public record PaymentProcessedMessage(
        UUID paymentId,
        UUID orderId,
        String status) {
}
