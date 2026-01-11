package br.com.postech.soat.infrastructure.http.mapper;

import br.com.postech.soat.openapi.model.CategoryDto;
import br.com.postech.soat.openapi.model.DiscountDto;
import br.com.postech.soat.openapi.model.GetOrders200ResponseInnerDto;
import br.com.postech.soat.openapi.model.OrderItemDto;
import br.com.postech.soat.openapi.model.OrderStatusDto;
import br.com.postech.soat.openapi.model.PostOrders201ResponseDiscountsInnerDto;
import br.com.postech.soat.openapi.model.PostOrders201ResponseDto;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.entity.OrderItem;
import br.com.postech.soat.domain.valueobject.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = { OrderStatusDto.class, CategoryDto.class })
public interface OrderResponseMapper {
    OrderResponseMapper INSTANCE = Mappers.getMapper(OrderResponseMapper.class);

    @Mapping(target = "orderId", source = "id.value")
    @Mapping(target = "status", expression = "java(OrderStatusDto.fromValue(order.getStatus().name()))")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "discounts", source = "discounts")
    @Mapping(target = "discountAmountTotal", expression = "java(order.getDiscountAmount().doubleValue())")
    @Mapping(target = "subtotal", expression = "java(order.getOriginalPrice().doubleValue())")
    @Mapping(target = "total", expression = "java(order.getTotalPrice().doubleValue())")
    PostOrders201ResponseDto toResponse(Order order);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "discount", source = "discount")
    @Mapping(target = "quantity", source = "quantity")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    default DiscountDto toDiscountDto(Discount discount) {
        if (discount == null) { return null; }

        return new DiscountDto()
                .value(discount.getValue().doubleValue());
    }

    default PostOrders201ResponseDiscountsInnerDto toDiscountInnerDto(Discount discount) {
        if (discount == null) { return null; }
        
        return new PostOrders201ResponseDiscountsInnerDto()
                .amount(discount.getValue().doubleValue());
    }

    @Mapping(target = "orderId", source = "id.value")
    @Mapping(target = "customerId", source = "customerId.value")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "discountAmountTotal", expression = "java(order.getDiscountAmount().doubleValue())")
    @Mapping(target = "total", expression = "java(order.getTotalPrice().doubleValue())")
    @Mapping(target = "status", expression = "java(OrderStatusDto.fromValue(order.getStatus().name()))")
    GetOrders200ResponseInnerDto toListResponse(Order order);
}