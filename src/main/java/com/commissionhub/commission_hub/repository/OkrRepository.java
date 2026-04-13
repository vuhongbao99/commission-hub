package com.commissionhub.commission_hub.repository;

import com.commissionhub.commission_hub.entity.Okr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OkrRepository extends JpaRepository<Okr, String> {
    Optional<Okr> findByYearAndTargetTypeAndTargetId(
            Integer year, String targetType, String targetId);
    List<Okr> findByYear(Integer year);
    List<Okr> findByYearAndTargetType(Integer year, String targetType);
}