package io.spring.up.secure;

import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EnhancementAuthority implements ComplexAuthority {
    private static final long serialVersionUID = 500L;
    private final String role;
    private final ConcurrentMap<String, Object> addtional =
            new ConcurrentHashMap<>();

    public EnhancementAuthority(final String role) {
        Assert.hasText(role, "[ UP ] A granted authority textual representation is required");
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    @Override
    public Object getValue(final String key) {
        return this.addtional.get(key);
    }

    @Override
    public ComplexAuthority putValue(final String key, final Object value) {
        this.addtional.put(key, value);
        return this;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof EnhancementAuthority ? this.role.equals(((EnhancementAuthority) obj).role) : false;
        }
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }
}
