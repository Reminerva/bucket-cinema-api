package com.flix.flix.service.impl;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.flix.flix.entity.AppUser;
import com.flix.flix.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{

    private final AppUserRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser userAccount = userAccountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        System.out.println("NIININININ: " + userAccount.getRole().toString());
        return new User(
            userAccount.getEmail(),
            userAccount.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority(userAccount.getRole().toString().substring(1, userAccount.getRole().toString().length() - 1)))
        );
    }

}
