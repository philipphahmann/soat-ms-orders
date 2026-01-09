package br.com.postech.soat.application.repositories;

import br.com.postech.soat.commons.application.Pagination;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderStatus;
import br.com.postech.soat.domain.valueobject.OrderId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository {

    Order save(Order order);

    List<Order> findActiveOrdersSorted(Set<OrderStatus> orderStatuses, Pagination pagination);

    Optional<Order> findById(OrderId orderId);
}
