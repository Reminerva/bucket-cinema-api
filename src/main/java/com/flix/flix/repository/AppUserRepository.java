package com.flix.flix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.flix.flix.entity.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String>, JpaSpecificationExecutor<AppUser> {
    Optional<AppUser> findByEmail(String email); 
}
