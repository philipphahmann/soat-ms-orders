package br.com.postech.soat.infrastructure.persistence;

import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.commons.application.Pagination;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderItem;
import br.com.postech.soat.domain.entity.OrderStatus;
import br.com.postech.soat.domain.valueobject.CustomerId;
import br.com.postech.soat.domain.valueobject.Discount;
import br.com.postech.soat.domain.valueobject.OrderId;
import br.com.postech.soat.domain.valueobject.OrderItemId;
import br.com.postech.soat.infrastructure.persistence.entity.OrderEntity;
import br.com.postech.soat.infrastructure.persistence.entity.OrderItemEntity;
import br.com.postech.soat.infrastructure.persistence.jpa.OrderItemJpaRepository;
import br.com.postech.soat.infrastructure.persistence.jpa.OrderJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private OrderItemJpaRepository orderItemJpaRepository;

    @Test
    void save_ShouldPersistOrderAndItems() {
        // Cenário
        OrderId orderId = new OrderId(UUID.randomUUID());
        OrderItem item = new OrderItem(UUID.randomUUID(), "Burger", 1, BigDecimal.TEN, "Food", new Discount(BigDecimal.ZERO));
        Order order = new Order(orderId, new CustomerId(UUID.randomUUID()), OrderStatus.RECEIVED, BigDecimal.TEN, BigDecimal.ZERO, Collections.emptyList(), List.of(item));

        when(orderJpaRepository.save(any(OrderEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(orderItemJpaRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // Execução
        Order saved = orderRepository.save(order);

        // Verificação
        Assertions.assertNotNull(saved);
        verify(orderJpaRepository).save(any(OrderEntity.class));
        verify(orderItemJpaRepository).saveAll(anyList());
    }

    @Test
    void findActiveOrdersSorted_ShouldReturnDomainOrders() {
        Pagination pagination = new Pagination(0, 10);
        OrderEntity entity = new OrderEntity();
        entity.setId(UUID.randomUUID());
        entity.setCustomerId(UUID.randomUUID());
        entity.setStatus(OrderStatus.RECEIVED);
        entity.setTotalPrice(BigDecimal.TEN);
        entity.setDiscountAmount(BigDecimal.ZERO);
        entity.setCreatedAt(Instant.now());

        when(orderJpaRepository.findActiveOrdersSorted(any(), any(Pageable.class)))
                .thenReturn(List.of(entity));
        
        // Mock busca de itens
        when(orderItemJpaRepository.findByOrderId(any())).thenReturn(Collections.emptyList());

        List<Order> result = orderRepository.findActiveOrdersSorted(Collections.singleton(OrderStatus.RECEIVED), pagination);

        Assertions.assertFalse(result.isEmpty());
        verify(orderItemJpaRepository).findByOrderId(entity.getId());
    }

    @Test
    void findById_WhenExists_ShouldReturnOrder() {
        // Cenário
        UUID id = UUID.randomUUID();
        OrderId orderId = new OrderId(id);
        
        OrderEntity entity = new OrderEntity();
        entity.setId(id);
        entity.setCustomerId(UUID.randomUUID());
        entity.setStatus(OrderStatus.RECEIVED);
        entity.setTotalPrice(BigDecimal.TEN);
        entity.setDiscountAmount(BigDecimal.ZERO);
        entity.setCreatedAt(Instant.now());

        when(orderJpaRepository.findById(id)).thenReturn(Optional.of(entity));
        // Mock dos itens é necessário pois o findById busca eles
        when(orderItemJpaRepository.findByOrderId(id)).thenReturn(Collections.emptyList());

        // Execução
        Optional<Order> result = orderRepository.findById(orderId);

        // Verificação
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(id, result.get().getId().getValue());
        verify(orderJpaRepository).findById(id);
        verify(orderItemJpaRepository).findByOrderId(id);
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        // Cenário
        UUID id = UUID.randomUUID();
        OrderId orderId = new OrderId(id);

        when(orderJpaRepository.findById(id)).thenReturn(Optional.empty());

        // Execução
        Optional<Order> result = orderRepository.findById(orderId);

        // Verificação
        Assertions.assertTrue(result.isEmpty());
        verify(orderJpaRepository).findById(id);
        // Garante que não tentou buscar itens se o pedido não existe
        verify(orderItemJpaRepository, never()).findByOrderId(any());
    }
}