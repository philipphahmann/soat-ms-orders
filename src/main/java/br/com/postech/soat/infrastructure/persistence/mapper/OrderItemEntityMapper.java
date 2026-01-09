package br.com.postech.soat.infrastructure.persistence.mapper;

import br.com.postech.soat.domain.entity.OrderItem;
import br.com.postech.soat.domain.valueobject.Discount;
import br.com.postech.soat.infrastructure.persistence.entity.OrderItemEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderItemEntityMapper {
    OrderItemEntityMapper INSTANCE = Mappers.getMapper(OrderItemEntityMapper.class);

    default OrderItem toDomain(OrderItemEntity orderItemEntity) {
        if (orderItemEntity == null) {
            return null;
        }

        Discount discount = new Discount(orderItemEntity.getDiscountAmount());

        return new OrderItem(
                orderItemEntity.getProductId(),
                orderItemEntity.getProductName(),
                orderItemEntity.getProductQuantity(),
                orderItemEntity.getUnitPrice(),
                orderItemEntity.getProductCategory(),
                discount);
    }

    default List<OrderItem> toDomainList(List<OrderItemEntity> orderItemEntities) {
        if (orderItemEntities == null) {
            return null;
        }

        return orderItemEntities.stream()
                .map(this::toDomain)
                .toList();
    }
}