package br.com.postech.soat.domain.valueobject;

import br.com.postech.soat.commons.domain.Identifier;
import java.util.UUID;

public class OrderId extends Identifier {

    public OrderId(UUID value) {
        super(value);
    }
}
