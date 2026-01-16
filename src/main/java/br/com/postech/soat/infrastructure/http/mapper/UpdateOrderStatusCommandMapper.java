package br.com.postech.soat.infrastructure.http.mapper;

import br.com.postech.soat.application.command.UpdateOrderStatusCommand;
import br.com.postech.soat.openapi.model.PutOrdersRequestDto;
import br.com.postech.soat.domain.entity.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateOrderStatusCommandMapper {

    UpdateOrderStatusCommandMapper INSTANCE =
            Mappers.getMapper(UpdateOrderStatusCommandMapper.class);

    @Mapping(
            target = "status",
            expression = "java(OrderStatus.valueOf(dto.getStatus().toUpperCase()))"
    )
    UpdateOrderStatusCommand mapFrom(String orderId, PutOrdersRequestDto dto);
}
