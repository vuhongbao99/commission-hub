package com.commissionhub.commission_hub.service;

import com.commissionhub.commission_hub.dto.request.HandoverRequest;
import com.commissionhub.commission_hub.dto.request.ResignRequest;
import com.commissionhub.commission_hub.dto.response.ResignSummaryResponse;
import com.commissionhub.commission_hub.dto.response.TaskResponse;
import com.commissionhub.commission_hub.entity.*;
import com.commissionhub.commission_hub.enums.*;
import com.commissionhub.commission_hub.exception.BadRequestException;
import com.commissionhub.commission_hub.exception.NotFoundException;
import com.commissionhub.commission_hub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResignService {

    private final UserRepository userRepository;
    private final CommissionTaskRepository commissionTaskRepository;
    private final NotificationRepository notificationRepository;
    private final PaymentVoucherRepository paymentVoucherRepository;

    @Transactional
    public ResignSummaryResponse resignUser(String userId, ResignRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên"));

        user.setStatus(UserStatus.RESIGNED);
        user.setResignedAt(request.getResignedAt() != null ? request.getResignedAt() : LocalDate.now());
        userRepository.save(user);

        List<CommissionTask> allTasks = commissionTaskRepository.findAll().stream()
                .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getId().equals(userId))
                .collect(Collectors.toList());

        List<CommissionTask> pendingTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING).collect(Collectors.toList());
        List<CommissionTask> approvedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.APPROVED).collect(Collectors.toList());
        List<CommissionTask> inProgressTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).collect(Collectors.toList());

        Long totalPendingAmount = pendingTasks.stream().mapToLong(CommissionTask::getCommissionAmount).sum()
                + approvedTasks.stream().mapToLong(CommissionTask::getCommissionAmount).sum();

        return ResignSummaryResponse.builder()
                .userId(userId).fullName(user.getFullName())
                .resignedAt(user.getResignedAt().toString())
                .pendingTasks(pendingTasks.stream().map(this::mapTask).collect(Collectors.toList()))
                .approvedTasks(approvedTasks.stream().map(this::mapTask).collect(Collectors.toList()))
                .inProgressTasks(inProgressTasks.stream().map(this::mapTask).collect(Collectors.toList()))
                .totalPendingAmount(totalPendingAmount)
                .build();
    }

    @Transactional
    public String handleHandover(HandoverRequest request) {
        CommissionTask task = commissionTaskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy task"));

        switch (request.getAction()) {
            case PAY_NOW -> {
                task.setStatus(TaskStatus.PAID);
                task.setPaymentDate(LocalDate.now());
                commissionTaskRepository.save(task);
                notificationRepository.save(Notification.builder()
                        .user(task.getAssignedTo())
                        .type(NotificationType.PAYMENT_COMPLETED)
                        .title("Hoa hồng đã được thanh toán")
                        .message("Task \"" + task.getTaskName() + "\" đã được thanh toán trước khi nghỉ việc")
                        .refId(task.getId()).refType("commission_tasks").isRead(false).build());
                return "Đã thanh toán task: " + task.getTaskName();
            }
            case TRANSFER -> {
                if (request.getTransferToUserId() == null) {
                    throw new BadRequestException("Cần chỉ định nhân viên nhận bàn giao");
                }
                User newUser = userRepository.findById(request.getTransferToUserId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên nhận"));
                task.setTransferredTo(newUser);
                task.setAssignedTo(newUser);
                task.setStatus(TaskStatus.IN_PROGRESS);
                commissionTaskRepository.save(task);
                notificationRepository.save(Notification.builder()
                        .user(newUser).type(NotificationType.HANDOVER_REQUIRED)
                        .title("Bạn nhận bàn giao task")
                        .message("Task \"" + task.getTaskName() + "\" được chuyển giao cho bạn")
                        .refId(task.getId()).refType("commission_tasks").isRead(false).build());
                return "Đã chuyển giao task cho: " + newUser.getFullName();
            }
            case CANCEL -> {
                task.setStatus(TaskStatus.REJECTED);
                task.setRejectionReason("Huỷ do nhân viên nghỉ việc");
                commissionTaskRepository.save(task);
                return "Đã huỷ task: " + task.getTaskName();
            }
            default -> throw new BadRequestException("Action không hợp lệ");
        }
    }

    private TaskResponse mapTask(CommissionTask t) {
        return TaskResponse.builder()
                .id(t.getId()).taskName(t.getTaskName())
                .commissionAmount(t.getCommissionAmount())
                .status(t.getStatus().name()).taskCreatedDate(t.getTaskCreatedDate())
                .build();
    }
}