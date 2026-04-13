package com.commissionhub.commission_hub.service;

import com.commissionhub.commission_hub.entity.Notification;
import com.commissionhub.commission_hub.entity.User;
import com.commissionhub.commission_hub.exception.NotFoundException;
import com.commissionhub.commission_hub.repository.NotificationRepository;
import com.commissionhub.commission_hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));
    }

    public List<Notification> getMyNotifications() {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(getCurrentUser().getId());
    }

    public long countUnread() {
        return notificationRepository.countByUserIdAndIsReadFalse(getCurrentUser().getId());
    }

    @Transactional
    public void markAllRead() {
        List<Notification> notis = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(getCurrentUser().getId());
        notis.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notis);
    }
}