package io.spring.up.epic;

import io.spring.up.epic.fn.Fn;
import io.spring.up.exception.web._401UnsupportedAuthorityException;
import io.spring.up.secure.ComplexAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class Secure {

    private static final String ANONYMOUS = "ROLE_ANONYMOUS";

    static Optional<String> getCurrentUserLogin() {
        final SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(context.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        final UserDetails user = (UserDetails) authentication.getPrincipal();
                        return user.getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        return (String) authentication.getPrincipal();
                    }
                    return null;
                });
    }

    private static User getCurrentUser() {
        final SecurityContext context = SecurityContextHolder.getContext();
        return Fn.getNull(() -> {
            final Authentication authentication = context.getAuthentication();
            return Fn.getNull(() ->
                            (authentication.getPrincipal() instanceof UserDetails)
                                    ? (User) authentication.getPrincipal()
                                    : null
                    , authentication);
        }, context);
    }

    static <T> T getUniqueAuthority(final String key) {
        final User user = getCurrentUser();
        return Fn.getNull(() -> {
            final Collection<GrantedAuthority> authorites = user.getAuthorities();
            T reference = null;
            if (null != authorites && !authorites.isEmpty()) {
                final List<GrantedAuthority> filtered = authorites.stream()
                        .filter(item -> item instanceof ComplexAuthority)
                        .collect(Collectors.toList());
                Fn.out(1 < filtered.size(), _401UnsupportedAuthorityException.class,
                        Secure.class, user.getUsername(), filtered.size());
                final GrantedAuthority filteredItem = filtered.get(0);
                if (null != filteredItem) {
                    final ComplexAuthority authority = (ComplexAuthority) filteredItem;
                    final Object ret = authority.getValue(key);
                    reference = null == ret ? null : (T) ret;
                }
            }
            return reference;
        }, user, key);
    }

    static boolean isAuthenticated() {
        final SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(context.getAuthentication())
                .map(authentication -> authentication.getAuthorities().stream()
                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ANONYMOUS)))
                .orElse(false);
    }

    static boolean isInRole(final String authority) {
        final SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(context.getAuthentication())
                .map(authentication -> authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)))
                .orElse(false);
    }
}
