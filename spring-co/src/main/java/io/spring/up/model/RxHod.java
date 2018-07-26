package io.spring.up.model;

import io.spring.up.epic.fn.Fn;

public class RxHod {

    private Object reference;

    public <T> RxHod add(final T reference) {
        this.reference = reference;
        return this;
    }

    public boolean successed() {
        return null != this.reference;
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
        return Fn.getNull(() -> (T) this.reference, this.reference);
    }
}
