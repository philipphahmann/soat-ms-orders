package br.com.postech.soat.infrastructure.messaging.dto;

import br.com.postech.soat.domain.entity.OrderStatus;

public enum PaymentStatusMessage {
    APPROVED,
    FINISHED,
    DECLINED,
    FAILED;

    public static PaymentStatusMessage from(String value) {
        try {
            return PaymentStatusMessage.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown payment status received: " + value, ex);
        }
    }

    public OrderStatus toOrderStatus() {
        return switch (this) {
            case APPROVED, FINISHED -> OrderStatus.PAID;
            case DECLINED, FAILED -> OrderStatus.AWAITING_PAYMENT;
        };
    }
}
