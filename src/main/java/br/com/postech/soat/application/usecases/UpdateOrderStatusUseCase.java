package br.com.postech.soat.application.usecases;

import br.com.postech.soat.application.command.UpdateOrderStatusCommand;
import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderStatus;
import br.com.postech.soat.domain.valueobject.OrderId;

public class UpdateOrderStatusUseCase {

    private final OrderRepository orderRepository;

    public UpdateOrderStatusUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order execute(UpdateOrderStatusCommand command) {
        Order order = orderRepository.findById(new OrderId(command.getOrderId()))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(OrderStatus.valueOf(command.getStatus().name()));

        return orderRepository.updateStatus(order);
    }
}
