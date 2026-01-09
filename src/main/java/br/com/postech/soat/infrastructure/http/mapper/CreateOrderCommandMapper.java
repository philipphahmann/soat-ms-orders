package br.com.postech.soat.infrastructure.http.mapper;

import br.com.postech.soat.openapi.model.PostOrdersRequestDto;
import br.com.postech.soat.application.command.CreateOrderCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        NoteMapper.class,
        OrderItemMapper.class,
        DiscountMapper.class
})
public interface CreateOrderCommandMapper {

    CreateOrderCommandMapper INSTANCE = Mappers.getMapper(CreateOrderCommandMapper.class);

    @Mapping(target = "cpf", source = "request.customerId")
    @Mapping(target = "observations", expression = "java(NoteMapper.INSTANCE.mapFrom(request.getNotes()))")
    @Mapping(target = "orderItems", source = "request.items")
    @Mapping(target = "discounts", source = "request.discounts")
    CreateOrderCommand mapFrom(PostOrdersRequestDto request);
}
