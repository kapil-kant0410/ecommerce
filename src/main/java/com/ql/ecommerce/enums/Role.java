package com.ql.ecommerce.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public enum Role{
    ROLE_ADMIN,
    ROLE_SELLER,
    ROLE_CUSTOMER;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.name()));
    }

}
