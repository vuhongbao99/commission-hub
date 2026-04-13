package com.commissionhub.commission_hub.service;

import com.commissionhub.commission_hub.dto.request.*;
import com.commissionhub.commission_hub.dto.response.*;
import com.commissionhub.commission_hub.entity.*;
import com.commissionhub.commission_hub.enums.*;
import com.commissionhub.commission_hub.exception.BadRequestException;
import com.commissionhub.commission_hub.exception.NotFoundException;
import com.commissionhub.commission_hub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepository commissionRepository;
    private final CommissionTaskRepository commissionTaskRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SystemConfigRepository systemConfigRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));
    }

    private int getOverdueDays() {
        return systemConfigRepository.findByConfigKey("task_overdue_days")
                .map(c -> Integer.parseInt(c.getConfigValue()))
                .orElse(30);
    }

    @Transactional
    public CommissionResponse createCommission(CreateCommissionRequest request) {
        User user = getCurrentUser();

        if (commissionRepository.findByUserIdAndPeriodMonthAndPeriodYear(
                user.getId(), request.getPeriodMonth(), request.getPeriodYear()).isPresent()) {
            throw new BadRequestException("Hoa hồng tháng " + request.getPeriodMonth()
                    + "/" + request.getPeriodYear() + " đã tồn tại");
        }

        Commission commission = Commission.builder()
                .user(user)
                .periodMonth(request.getPeriodMonth())
                .periodYear(request.getPeriodYear())
                .revenue(request.getRevenue())
                .rate(user.getCommissionRate())
                .amount(calculateAmount(request.getRevenue(), user.getCommissionRate()))
                .status(CommissionStatus.DRAFT)
                .note(request.getNote())
                .build();

        commissionRepository.save(commission);
        return mapToCommissionResponse(commission);
    }

    @Transactional
    public TaskResponse createTask(String commissionId, CreateTaskRequest request) {
        User user = getCurrentUser();

        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hoa hồng"));

        if (!commission.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền thêm task vào hoa hồng này");
        }

        CommissionTask task = CommissionTask.builder()
                .commission(commission)
                .taskName(request.getTaskName())
                .taskLink(request.getTaskLink())
                .commissionAmount(request.getCommissionAmount())
                .assignedTo(user)
                .taskCreatedDate(request.getTaskCreatedDate())
                .status(TaskStatus.IN_PROGRESS)
                .build();

        commissionTaskRepository.save(task);
        return mapToTaskResponse(task);
    }

    @Transactional
    public TaskResponse completeTask(String taskId, LocalDate completedDate) {
        User user = getCurrentUser();

        CommissionTask task = commissionTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy task"));

        if (!task.getAssignedTo().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền hoàn thành task này");
        }

        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new BadRequestException("Task không ở trạng thái IN_PROGRESS");
        }

        task.setTaskCompletedDate(completedDate);
        task.setCompletedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.PENDING);
        commissionTaskRepository.save(task);

        userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.DIRECTOR || u.getRole() == Role.ADMIN)
                .forEach(director -> {
                    Notification noti = Notification.builder()
                            .user(director)
                            .type(NotificationType.TASK_COMPLETED)
                            .title("Task cần duyệt")
                            .message(user.getFullName() + " đã hoàn thành task: " + task.getTaskName())
                            .refId(task.getId())
                            .refType("commission_tasks")
                            .isRead(false)
                            .build();
                    notificationRepository.save(noti);
                });

        return mapToTaskResponse(task);
    }

    @Transactional
    public TaskResponse approveTask(String taskId) {
        CommissionTask task = commissionTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy task"));

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new BadRequestException("Task không ở trạng thái PENDING");
        }

        task.setStatus(TaskStatus.APPROVED);
        commissionTaskRepository.save(task);

        Notification noti = Notification.builder()
                .user(task.getAssignedTo())
                .type(NotificationType.TASK_APPROVED)
                .title("Task đã được duyệt")
                .message("Task \"" + task.getTaskName() + "\" đã được duyệt, chờ thanh toán")
                .refId(task.getId())
                .refType("commission_tasks")
                .isRead(false)
                .build();
        notificationRepository.save(noti);

        return mapToTaskResponse(task);
    }

    @Transactional
    public TaskResponse rejectTask(String taskId, RejectTaskRequest request) {
        CommissionTask task = commissionTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy task"));

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new BadRequestException("Task không ở trạng thái PENDING");
        }

        task.setStatus(TaskStatus.REJECTED);
        task.setRejectionReason(request.getRejectionReason());
        commissionTaskRepository.save(task);

        Notification noti = Notification.builder()
                .user(task.getAssignedTo())
                .type(NotificationType.TASK_REJECTED)
                .title("Task bị từ chối")
                .message("Task \"" + task.getTaskName() + "\" bị từ chối: " + request.getRejectionReason())
                .refId(task.getId())
                .refType("commission_tasks")
                .isRead(false)
                .build();
        notificationRepository.save(noti);

        return mapToTaskResponse(task);
    }

    public List<CommissionResponse> getMyCommissions() {
        User user = getCurrentUser();
        return commissionRepository
                .findByUserIdOrderByPeriodYearDescPeriodMonthDesc(user.getId())
                .stream().map(this::mapToCommissionResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getPendingTasks() {
        return commissionTaskRepository.findByStatus(TaskStatus.PENDING)
                .stream().map(this::mapToTaskResponse).collect(Collectors.toList());
    }

    public List<CommissionResponse> getAllCommissions() {
        return commissionRepository.findAll().stream()
                .map(this::mapToCommissionResponse).collect(Collectors.toList());
    }

    public CommissionResponse getCommissionById(String id) {
        return mapToCommissionResponse(commissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hoa hồng")));
    }

    public TaskResponse getTaskById(String taskId) {
        return mapToTaskResponse(commissionTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy task")));
    }

    public List<TaskResponse> getOverdueTasks() {
        return commissionTaskRepository.findOverdueTasks(getOverdueDays())
                .stream().map(this::mapToTaskResponse).collect(Collectors.toList());
    }

    private Long calculateAmount(Long revenue, java.math.BigDecimal rate) {
        return revenue * rate.longValue() / 100;
    }

    private CommissionResponse mapToCommissionResponse(Commission c) {
        List<TaskResponse> tasks = commissionTaskRepository.findByCommissionId(c.getId())
                .stream().map(this::mapToTaskResponse).collect(Collectors.toList());

        return CommissionResponse.builder()
                .id(c.getId())
                .userId(c.getUser().getId())
                .fullName(c.getUser().getFullName())
                .periodMonth(c.getPeriodMonth())
                .periodYear(c.getPeriodYear())
                .revenue(c.getRevenue())
                .rate(c.getRate())
                .amount(c.getAmount())
                .status(c.getStatus().name())
                .tasks(tasks)
                .build();
    }

    private TaskResponse mapToTaskResponse(CommissionTask t) {
        Long overdueDays = null;
        if (t.getTaskCompletedDate() == null && t.getStatus() == TaskStatus.IN_PROGRESS) {
            overdueDays = ChronoUnit.DAYS.between(t.getTaskCreatedDate(), LocalDate.now());
            if (overdueDays <= 0) overdueDays = null;
        }

        return TaskResponse.builder()
                .id(t.getId())
                .taskName(t.getTaskName())
                .taskLink(t.getTaskLink())
                .commissionAmount(t.getCommissionAmount())
                .assignedToName(t.getAssignedTo() != null ? t.getAssignedTo().getFullName() : null)
                .taskCreatedDate(t.getTaskCreatedDate())
                .taskCompletedDate(t.getTaskCompletedDate())
                .completedAt(t.getCompletedAt())
                .status(t.getStatus().name())
                .rejectionReason(t.getRejectionReason())
                .paymentDate(t.getPaymentDate())
                .overdueDays(overdueDays)
                .build();
    }
}