package com.commissionhub.commission_hub.service;

import com.commissionhub.commission_hub.dto.request.CreateVoucherRequest;
import com.commissionhub.commission_hub.dto.response.TaskResponse;
import com.commissionhub.commission_hub.dto.response.VoucherResponse;
import com.commissionhub.commission_hub.entity.CommissionTask;
import com.commissionhub.commission_hub.entity.Notification;
import com.commissionhub.commission_hub.entity.PaymentVoucher;
import com.commissionhub.commission_hub.entity.User;
import com.commissionhub.commission_hub.enums.NotificationType;
import com.commissionhub.commission_hub.enums.TaskStatus;
import com.commissionhub.commission_hub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentVoucherRepository paymentVoucherRepository;
    private final CommissionTaskRepository commissionTaskRepository;
    private final BulkPaymentBatchRepository bulkPaymentBatchRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }

    // ============================================================
    // 1. Tạo phiếu thanh toán
    // ============================================================
    @Transactional
    public VoucherResponse createVoucher(CreateVoucherRequest request) {
        User director = getCurrentUser();

        // Lấy danh sách task
        List<CommissionTask> tasks = request.getTaskIds().stream()
                .map(id -> commissionTaskRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Task không tồn tại: " + id)))
                .collect(Collectors.toList());

        // Kiểm tra tất cả task phải APPROVED
        tasks.forEach(t -> {
            if (t.getStatus() != TaskStatus.APPROVED) {
                throw new RuntimeException("Task " + t.getId() + " chưa được duyệt");
            }
        });

        // Kiểm tra tất cả task phải của cùng 1 nhân viên
        long distinctUsers = tasks.stream()
                .map(t -> t.getAssignedTo().getId())
                .distinct().count();
        if (distinctUsers > 1) {
            throw new RuntimeException("Tất cả task phải thuộc cùng 1 nhân viên");
        }

        User employee = tasks.get(0).getAssignedTo();
        Long totalAmount = tasks.stream()
                .mapToLong(CommissionTask::getCommissionAmount)
                .sum();

        // Sinh mã phiếu tự động: Q1-NVA-001
        String voucherCode = generateVoucherCode(employee);

        PaymentVoucher voucher = PaymentVoucher.builder()
                .voucherCode(voucherCode)
                .user(employee)
                .totalAmount(totalAmount)
                .createdBy(director)
                .build();

        paymentVoucherRepository.save(voucher);

        // Gắn voucher vào từng task
        tasks.forEach(t -> {
            t.setVoucher(voucher);
            commissionTaskRepository.save(t);
        });

        return mapToVoucherResponse(voucher, tasks);
    }

    // ============================================================
    // 2. Xem danh sách phiếu
    // ============================================================
    public List<VoucherResponse> getAllVouchers() {
        return paymentVoucherRepository.findAll().stream()
                .map(v -> mapToVoucherResponse(v,
                        commissionTaskRepository.findByVoucherId(v.getId())))
                .collect(Collectors.toList());
    }

    // ============================================================
    // 3. Import kết quả ngân hàng — đánh dấu PAID
    // ============================================================
    @Transactional
    public String confirmPayment(String voucherCode, Long billAmount) {
        PaymentVoucher voucher = paymentVoucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu: " + voucherCode));

        voucher.setBillAmount(billAmount);
        voucher.setIsMatched(billAmount.equals(voucher.getTotalAmount()));
        voucher.setPaidAt(LocalDateTime.now());
        paymentVoucherRepository.save(voucher);

        // Cập nhật tất cả task → PAID
        List<CommissionTask> tasks = commissionTaskRepository
                .findByVoucherId(voucher.getId());

        tasks.forEach(t -> {
            t.setStatus(TaskStatus.PAID);
            t.setPaymentDate(LocalDate.now());
            commissionTaskRepository.save(t);
        });

        // Gửi notification cho nhân viên
        Notification noti = Notification.builder()
                .user(voucher.getUser())
                .type(NotificationType.PAYMENT_COMPLETED)
                .title("Hoa hồng đã được thanh toán")
                .message("Phiếu " + voucherCode + " đã được thanh toán: "
                        + String.format("%,.0f", billAmount.doubleValue()) + " VNĐ")
                .refId(voucher.getId())
                .refType("payment_vouchers")
                .isRead(false)
                .build();
        notificationRepository.save(noti);

        return voucher.getIsMatched()
                ? "Thanh toán thành công — số tiền khớp"
                : "Thanh toán xong nhưng số tiền KHÔNG khớp — cần kiểm tra lại";
    }

    // ============================================================
    // Helper
    // ============================================================
    private String generateVoucherCode(User employee) {
        // Lấy quý hiện tại
        int month = LocalDate.now().getMonthValue();
        int quarter = (month - 1) / 3 + 1;

        // Lấy 3 chữ cái đầu tên nhân viên
        String[] nameParts = employee.getFullName().split(" ");
        StringBuilder initials = new StringBuilder();
        for (String part : nameParts) {
            if (!part.isEmpty()) initials.append(part.charAt(0));
        }
        String prefix = "Q" + quarter + "-" + initials.toString().toUpperCase();

        // Đếm số phiếu đã có để tăng số thứ tự
        long count = paymentVoucherRepository.findByUserId(employee.getId()).size() + 1;
        return prefix + "-" + String.format("%03d", count);
    }

    private VoucherResponse mapToVoucherResponse(PaymentVoucher v, List<CommissionTask> tasks) {
        List<TaskResponse> taskResponses = tasks.stream()
                .map(t -> TaskResponse.builder()
                        .id(t.getId())
                        .taskName(t.getTaskName())
                        .commissionAmount(t.getCommissionAmount())
                        .status(t.getStatus().name())
                        .paymentDate(t.getPaymentDate())
                        .build())
                .collect(Collectors.toList());

        return VoucherResponse.builder()
                .id(v.getId())
                .voucherCode(v.getVoucherCode())
                .userFullName(v.getUser().getFullName())
                .bankName(v.getUser().getBankName())
                .bankAccount(v.getUser().getBankAccount())
                .totalAmount(v.getTotalAmount())
                .isMatched(v.getIsMatched())
                .paidAt(v.getPaidAt())
                .tasks(taskResponses)
                .build();
    }
}