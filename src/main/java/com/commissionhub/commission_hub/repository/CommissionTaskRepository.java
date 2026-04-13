package com.commissionhub.commission_hub.repository;


import com.commissionhub.commission_hub.entity.CommissionTask;
import com.commissionhub.commission_hub.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommissionTaskRepository extends JpaRepository<CommissionTask, String> {

    List<CommissionTask> findByCommissionId(String commissionId);
    List<CommissionTask> findByStatus(TaskStatus status);
    List<CommissionTask> findByVoucherId(String voucherId);

    @Query("""
        SELECT t FROM CommissionTask t
        WHERE t.taskCompletedDate IS NULL
        AND t.status = 'IN_PROGRESS'
        AND DATEDIFF(CURRENT_DATE, t.taskCreatedDate) > :days
        ORDER BY t.taskCreatedDate ASC
    """)
    List<CommissionTask> findOverdueTasks(@Param("days") int days);
}