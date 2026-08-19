package com.shiba.auth.data.repository;

import com.shiba.auth.data.entity.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {

    @Transactional(readOnly = true)
    Optional<CredentialEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
