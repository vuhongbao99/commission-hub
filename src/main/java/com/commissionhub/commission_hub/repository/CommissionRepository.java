package com.commissionhub.commission_hub.repository;

import com.commissionhub.commission_hub.entity.Commission;
import com.commissionhub.commission_hub.enums.CommissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, String> {

    Optional<Commission> findByUserIdAndPeriodMonthAndPeriodYear(
            String userId, Integer month, Integer year);

    List<Commission> findByUserIdOrderByPeriodYearDescPeriodMonthDesc(String userId);

    List<Commission> findByStatus(CommissionStatus status);



    @Query("SELECT SUM(c.amount) FROM Commission c WHERE c.user.id = :userId AND c.periodYear = :year")
    Long sumAmountByUserAndYear(@Param("userId") String userId, @Param("year") Integer year);

}