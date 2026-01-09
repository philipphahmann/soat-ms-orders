package br.com.postech.soat.application.usecases;

import br.com.postech.soat.commons.application.Pagination;
import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderStatus;
import java.util.List;

public class ListActiveOrdersUseCase {

    private final OrderRepository orderRepository;

    public ListActiveOrdersUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> execute(Pagination pagination) {
        return orderRepository.findActiveOrdersSorted(OrderStatus.activeOrderStatusList(), pagination);
    }
}