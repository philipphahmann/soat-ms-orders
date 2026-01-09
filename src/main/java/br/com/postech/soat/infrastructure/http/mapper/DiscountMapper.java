package br.com.postech.soat.infrastructure.http.mapper;

import br.com.postech.soat.openapi.model.DiscountDto;
import br.com.postech.soat.domain.valueobject.Discount;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DiscountMapper {

    DiscountMapper INSTANCE = Mappers.getMapper(DiscountMapper.class);

    @Mapping(target = "value", expression = "java(new BigDecimal(discountDto.getValue()))")
    Discount mapFrom(DiscountDto discountDto);

    default List<Discount> mapFrom(List<DiscountDto> discountDtos) {
        if (discountDtos == null) {
            return null;
        }
        return discountDtos.stream()
                .map(this::mapFrom)
                .toList();
    }
}