package com.commissionhub.commission_hub.repository;

import com.commissionhub.commission_hub.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    boolean existsByTeamName(String teamName);
}