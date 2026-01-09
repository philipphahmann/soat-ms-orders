package br.com.postech.soat.commons.domain;

public abstract class AggregateRoot<ID extends Identifier> {
    private final ID id;

    protected AggregateRoot(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }
}
