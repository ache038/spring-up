package io.spring.up.secure;

import org.springframework.security.core.GrantedAuthority;

public interface ComplexAuthority extends GrantedAuthority {

    @Override
    String getAuthority();

    Object getValue(String key);

    ComplexAuthority putValue(String key, Object value);
}
