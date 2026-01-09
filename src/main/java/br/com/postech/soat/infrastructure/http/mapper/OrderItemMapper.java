package br.com.postech.soat.infrastructure.http.mapper;

import br.com.postech.soat.openapi.model.OrderItemDto;
import br.com.postech.soat.domain.entity.OrderItem;
import br.com.postech.soat.infrastructure.persistence.entity.OrderItemEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { DiscountMapper.class }, imports = { BigDecimal.class, DiscountMapper.class })
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", expression = "java(new BigDecimal(orderItemDto.getPrice()))")
    @Mapping(target = "discount", source = "discount")
    @Mapping(target = "category", expression = "java(orderItemDto.getCategory() != null ? orderItemDto.getCategory().getValue() : null)")
    @Mapping(target = "productId", source = "productId")
    OrderItem toDomain(OrderItemDto orderItemDto);

    @Mapping(target = "id", source = "orderItem.id.value")
    @Mapping(target = "productId", source = "orderItem.productId")
    @Mapping(target = "discountAmount", source = "orderItem.discount.value")
    @Mapping(target = "productName", source = "orderItem.name")
    @Mapping(target = "productQuantity", source = "orderItem.quantity")
    @Mapping(target = "productCategory", source = "orderItem.category")
    @Mapping(target = "unitPrice", source = "orderItem.price")
    OrderItemEntity toEntity(OrderItem orderItem, UUID orderId);

    default List<OrderItem> toDomain(List<OrderItemDto> orderItemDtos) {
        if (orderItemDtos == null) {
            return null;
        }
        return orderItemDtos.stream()
                .map(this::toDomain)
                .toList();
    }
}
