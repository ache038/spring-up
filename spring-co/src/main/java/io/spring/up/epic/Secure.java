package io.spring.up.epic;

import io.spring.up.epic.fn.Fn;
import io.spring.up.exception.web._401SegmentAuthorityException;
import io.spring.up.exception.web._401UnsupportedAuthorityException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    static String getUniqueAuthority() {
        final SecurityContext context = SecurityContextHolder.getContext();
        final Authentication authentication = context.getAuthentication();
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authentication.getAuthorities().forEach(item -> authorities.add((GrantedAuthority) item));
        String reference = null;
        Fn.out(1 < authorities.size(), _401UnsupportedAuthorityException.class,
                Secure.class, Secure.getCurrentUserLogin().get(), authorities.size());
        if (0 < authorities.size()) {
            final GrantedAuthority authority = authorities.get(0);
            if (null != authority) {
                reference = authority.getAuthority();
            }
        }
        return reference;
    }

    static String[] getAuthorities() {
        final String authority = getUniqueAuthority();
        String[] segments = new String[3];
        if (null != authority) {
            segments = getUniqueAuthority().split(":");
            Fn.out(3 != segments.length, _401SegmentAuthorityException.class,
                    Secure.class, segments.length);
        }
        return segments;
    }

    static String buildAuthority(final String userId, final String roleId, final String roleName) {
        return userId + ":" + roleName + ":" + roleId;
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
