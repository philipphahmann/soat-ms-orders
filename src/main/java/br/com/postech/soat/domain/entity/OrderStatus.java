package br.com.postech.soat.domain.entity;

import java.util.Set;

public enum OrderStatus {
    RECEIVED,
    AWAITING_PAYMENT,
    PAID,
    IN_PREPARATION,
    DONE,
    DELIVERED;

    public static Set<OrderStatus> activeOrderStatusList() {
        return Set.of(
                OrderStatus.DONE,
                OrderStatus.IN_PREPARATION,
                OrderStatus.RECEIVED);
    }
}
