package br.com.postech.soat.domain.valueobject;

import br.com.postech.soat.commons.domain.Identifier;
import java.util.UUID;

public class OrderItemId extends Identifier {

    public OrderItemId(UUID value) {
        super(value);
    }
}
