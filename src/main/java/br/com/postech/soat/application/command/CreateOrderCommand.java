package br.com.postech.soat.application.command;

import br.com.postech.soat.domain.entity.OrderItem;
import br.com.postech.soat.domain.valueobject.Discount;
import br.com.postech.soat.domain.valueobject.Observation;
import java.util.List;

public record CreateOrderCommand(
        String cpf,
        List<Discount> discounts,
        List<OrderItem> orderItems,
        List<Observation> observations) {
}
