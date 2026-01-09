package br.com.postech.soat.infrastructure.persistence.jpa;

import br.com.postech.soat.infrastructure.persistence.entity.OrderItemEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, UUID> {

    List<OrderItemEntity> findByOrderId(UUID orderId);
}
