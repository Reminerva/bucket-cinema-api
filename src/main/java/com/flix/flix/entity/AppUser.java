package com.flix.flix.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.flix.flix.constant.DbBash;
import com.flix.flix.constant.custom_enum.ERole;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = DbBash.USER_DB)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @ElementCollection(targetClass = ERole.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "t_app_user_roles",
        joinColumns = @jakarta.persistence.JoinColumn(name = "app_user_id")
    )
    @Column(name = "role")
    @Builder.Default
    private List<ERole> roles = new ArrayList<>();

    @OneToOne(mappedBy = "appUser")
    private Customer customer;

    @OneToOne(mappedBy = "appUser")
    private Employee employee;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<ERole> myRole = roles;
        return myRole.stream().map(userRole -> new SimpleGrantedAuthority(userRole.name())).toList();
    }

}
