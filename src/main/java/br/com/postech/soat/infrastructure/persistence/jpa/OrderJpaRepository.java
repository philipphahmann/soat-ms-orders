package br.com.postech.soat.infrastructure.persistence.jpa;

import br.com.postech.soat.domain.entity.OrderStatus;
import br.com.postech.soat.infrastructure.persistence.entity.OrderEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    @Query("""
            SELECT o FROM OrderEntity o
            WHERE o.status IN :activeStatusList
            ORDER BY
                CASE o.status
                    WHEN 'DONE' THEN 1
                    WHEN 'IN_PREPARATION' THEN 2
                    WHEN 'RECEIVED' THEN 3
                    ELSE 4
                END,
                o.createdAt ASC
            """)
    List<OrderEntity> findActiveOrdersSorted(Set<OrderStatus> activeStatusList, Pageable pageable);
}
