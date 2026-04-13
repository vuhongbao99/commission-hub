package com.commissionhub.commission_hub.service;

import com.commissionhub.commission_hub.entity.Notification;
import com.commissionhub.commission_hub.entity.PaymentVoucher;
import com.commissionhub.commission_hub.enums.TaskStatus;
import com.commissionhub.commission_hub.repository.CommissionTaskRepository;
import com.commissionhub.commission_hub.repository.NotificationRepository;
import com.commissionhub.commission_hub.repository.PaymentVoucherRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final PaymentVoucherRepository paymentVoucherRepository;
    private final CommissionTaskRepository commissionTaskRepository;
    private final NotificationRepository notificationRepository;

    // ============================================================
    // 1. Xuất file Excel bulk payment
    // ============================================================
    public byte[] exportBulkPayment() throws IOException {
        List<PaymentVoucher> vouchers = paymentVoucherRepository.findAll()
                .stream()
                .filter(v -> v.getPaidAt() == null)
                .toList();

        Workbook workbook = new XSSFWorkbook();

        // Tách theo từng ngân hàng
        List<String> banks = vouchers.stream()
                .map(v -> v.getUser().getBankName() != null ? v.getUser().getBankName() : "Khác")
                .distinct()
                .toList();

        for (String bank : banks) {
            Sheet sheet = workbook.createSheet(bank);

            // Header
            Row header = sheet.createRow(0);
            String[] cols = {"STT", "Họ tên", "Số tài khoản", "Ngân hàng", "Chi nhánh", "Số tiền", "Nội dung CK"};
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Data
            List<PaymentVoucher> bankVouchers = vouchers.stream()
                    .filter(v -> bank.equals(v.getUser().getBankName() != null
                            ? v.getUser().getBankName() : "Khác"))
                    .toList();

            int rowNum = 1;
            for (PaymentVoucher v : bankVouchers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(v.getUser().getFullName());
                row.createCell(2).setCellValue(v.getUser().getBankAccount() != null
                        ? v.getUser().getBankAccount() : "");
                row.createCell(3).setCellValue(v.getUser().getBankName() != null
                        ? v.getUser().getBankName() : "");
                row.createCell(4).setCellValue(v.getUser().getBankBranch() != null
                        ? v.getUser().getBankBranch() : "");
                row.createCell(5).setCellValue(v.getTotalAmount());
                row.createCell(6).setCellValue(v.getVoucherCode()); // Nội dung CK = mã phiếu
            }

            // Auto size columns
            for (int i = 0; i < cols.length; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    // ============================================================
    // 2. Import kết quả ngân hàng
    // ============================================================
    @Transactional
    public String importBankResult(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        int success = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                String voucherCode = row.getCell(0).getStringCellValue().trim();
                String status = row.getCell(1).getStringCellValue().trim();
                double amount = row.getCell(2).getNumericCellValue();

                PaymentVoucher voucher = paymentVoucherRepository
                        .findByVoucherCode(voucherCode).orElse(null);

                if (voucher == null) {
                    errors.add("Không tìm thấy phiếu: " + voucherCode);
                    failed++;
                    continue;
                }

                if ("SUCCESS".equalsIgnoreCase(status) || "THANH CONG".equalsIgnoreCase(status)) {
                    voucher.setBillAmount((long) amount);
                    voucher.setIsMatched((long) amount == voucher.getTotalAmount());
                    voucher.setPaidAt(LocalDateTime.now());
                    paymentVoucherRepository.save(voucher);

                    // Cập nhật task → PAID
                    commissionTaskRepository.findByVoucherId(voucher.getId())
                            .forEach(t -> {
                                t.setStatus(TaskStatus.PAID);
                                t.setPaymentDate(LocalDate.now());
                                commissionTaskRepository.save(t);
                            });

                    // Notification
                    Notification noti = Notification.builder()
                            .user(voucher.getUser())
                            .type(com.commissionhub.commission_hub.enums.NotificationType.PAYMENT_COMPLETED)
                            .title("Hoa hồng đã được thanh toán")
                            .message("Phiếu " + voucherCode + " thanh toán thành công")
                            .refId(voucher.getId())
                            .refType("payment_vouchers")
                            .isRead(false)
                            .build();
                    notificationRepository.save(noti);
                    success++;
                } else {
                    errors.add("Giao dịch thất bại: " + voucherCode + " — " + status);
                    failed++;
                }
            } catch (Exception e) {
                errors.add("Lỗi dòng " + i + ": " + e.getMessage());
                failed++;
            }
        }

        workbook.close();
        return "Kết quả import: " + success + " thành công, " + failed + " thất bại. "
                + (errors.isEmpty() ? "" : "Lỗi: " + String.join("; ", errors));
    }
}