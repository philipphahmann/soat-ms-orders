package br.com.postech.soat.domain.valueobject;

import br.com.postech.soat.commons.domain.Identifier;
import java.util.UUID;

public class CustomerId extends Identifier {

    public CustomerId(UUID value) {
        super(value);
    }
}
