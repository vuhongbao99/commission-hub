package com.commissionhub.commission_hub.repository;

import com.commissionhub.commission_hub.entity.User;
import com.commissionhub.commission_hub.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByStatus(UserStatus status);
    List<User> findByTeamId(String teamId);
}