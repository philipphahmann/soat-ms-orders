package br.com.postech.soat.infrastructure.persistence;

import br.com.postech.soat.commons.application.Pagination;
import br.com.postech.soat.commons.infrastructure.aop.monitorable.Monitorable;
import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderItem;
import br.com.postech.soat.domain.entity.OrderStatus;
import br.com.postech.soat.domain.valueobject.OrderId;
import br.com.postech.soat.infrastructure.http.mapper.OrderItemMapper;
import br.com.postech.soat.infrastructure.persistence.entity.OrderEntity;
import br.com.postech.soat.infrastructure.persistence.entity.OrderItemEntity;
import br.com.postech.soat.infrastructure.persistence.jpa.OrderItemJpaRepository;
import br.com.postech.soat.infrastructure.persistence.jpa.OrderJpaRepository;
import br.com.postech.soat.infrastructure.persistence.mapper.OrderEntityMapper;
import br.com.postech.soat.infrastructure.persistence.mapper.OrderItemEntityMapper;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Monitorable
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    private final Logger logger = LoggerFactory.getLogger(OrderRepositoryImpl.class);

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = OrderEntityMapper.INSTANCE.toEntity(order);
        orderJpaRepository.save(orderEntity);
        logger.info("Order saved : {}", orderEntity);

        List<OrderItemEntity> orderItemEntities = order.getOrderItems()
                .stream()
                .map(orderItem -> OrderItemMapper.INSTANCE.toEntity(orderItem, order.getId().getValue()))
                .toList();

        List<OrderItemEntity> orderItemEntitiesSaved = orderItemJpaRepository.saveAll(orderItemEntities);
        logger.info("Quantity order items saved : {}", orderItemEntitiesSaved.size());
        return order;
    }

    @Override
    public List<Order> findActiveOrdersSorted(Set<OrderStatus> activeOrderStatusList, Pagination pagination) {
        Pageable pageable = PageRequest.of(pagination.page(), pagination.size());
        List<OrderEntity> orderEntities = orderJpaRepository.findActiveOrdersSorted(activeOrderStatusList, pageable);

        logger.info("Found {} active orders with pagination - page: {}, size: {}",
                orderEntities.size(), pagination.page(), pagination.size());

        return orderEntities.stream()
                .map(orderEntity -> {
                    List<OrderItemEntity> orderItemEntities = orderItemJpaRepository.findByOrderId(orderEntity.getId());
                    List<OrderItem> orderItems = OrderItemEntityMapper.INSTANCE.toDomainList(orderItemEntities);

                    return OrderEntityMapper.INSTANCE.toDomain(orderEntity, orderItems);
                })
                .toList();
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        logger.debug("Finding order by id {}", orderId.getValue());
        return orderJpaRepository.findById(orderId.getValue())
                .map(orderEntity -> {
                    List<OrderItemEntity> orderItemEntities = orderItemJpaRepository.findByOrderId(orderEntity.getId());
                    List<OrderItem> orderItems = OrderItemEntityMapper.INSTANCE.toDomainList(orderItemEntities);
                    return OrderEntityMapper.INSTANCE.toDomain(orderEntity, orderItems);
                });
    }
}
