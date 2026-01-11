package br.com.postech.soat.infrastructure.http.mapper;

import br.com.postech.soat.openapi.model.DiscountDto;
import br.com.postech.soat.openapi.model.OrderItemDto;
import br.com.postech.soat.domain.entity.OrderItem;
import br.com.postech.soat.domain.valueobject.Discount;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = { BigDecimal.class })
public interface OrderItemRequestMapper {

    OrderItemRequestMapper INSTANCE = Mappers.getMapper(OrderItemRequestMapper.class);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "price", expression = "java(new BigDecimal(orderItemDto.getPrice()))")
    @Mapping(target = "discount", expression = "java(mapDiscount(orderItemDto.getDiscount()))")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "category", source = "category")
    OrderItem mapFrom(OrderItemDto orderItemDto);

    default Discount mapDiscount(DiscountDto dto) {
        if (dto == null)
            return null;
        return new Discount(BigDecimal.valueOf(dto.getValue()));
    }
}
