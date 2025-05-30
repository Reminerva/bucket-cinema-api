package com.flix.flix.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.flix.flix.constant.custom_enum.ERole;

public class AppUserTest {

    @Test
    void testGetAuthorities() {

        List<ERole> roles = Collections.singletonList(ERole.ROLE_ADMIN);
        AppUser appUser = AppUser.builder()
            .roles(roles)
            .build();

        Collection<? extends GrantedAuthority> authorities = appUser.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());

        GrantedAuthority expectedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        assertTrue(authorities.contains(expectedAuthority));
    }
}
