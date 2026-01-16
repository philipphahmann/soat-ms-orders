package br.com.postech.soat.application.command;

import br.com.postech.soat.domain.entity.OrderStatus;
import java.util.UUID;

public class UpdateOrderStatusCommand {

    private final UUID orderId;
    private final OrderStatus status;

    public UpdateOrderStatusCommand(UUID orderId, OrderStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }
}