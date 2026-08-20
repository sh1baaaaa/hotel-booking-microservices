package com.shiba.user.data.repository;

import com.shiba.user.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByIdOrEmailOrUsername(UUID id, String email, String username);

}
