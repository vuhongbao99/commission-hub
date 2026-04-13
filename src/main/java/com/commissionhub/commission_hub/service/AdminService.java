package com.commissionhub.commission_hub.service;

import com.commissionhub.commission_hub.dto.request.*;
import com.commissionhub.commission_hub.dto.response.UserResponse;
import com.commissionhub.commission_hub.entity.*;
import com.commissionhub.commission_hub.enums.UserStatus;
import com.commissionhub.commission_hub.exception.BadRequestException;
import com.commissionhub.commission_hub.exception.NotFoundException;
import com.commissionhub.commission_hub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse).collect(Collectors.toList());
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại: " + request.getEmail());
        }

        Team team = null;
        if (request.getTeamId() != null) {
            team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy team"));
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .team(team)
                .commissionRate(request.getCommissionRate())
                .bankName(request.getBankName())
                .bankAccount(request.getBankAccount())
                .bankBranch(request.getBankBranch())
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getCommissionRate() != null) user.setCommissionRate(request.getCommissionRate());
        if (request.getBankName() != null) user.setBankName(request.getBankName());
        if (request.getBankAccount() != null) user.setBankAccount(request.getBankAccount());
        if (request.getBankBranch() != null) user.setBankBranch(request.getBankBranch());

        if (request.getTeamId() != null) {
            Team team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy team"));
            user.setTeam(team);
        }

        userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateStatus(String userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        user.setStatus(UserStatus.valueOf(status));
        userRepository.save(user);
        return mapToUserResponse(user);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Transactional
    public Team createTeam(CreateTeamRequest request) {
        if (teamRepository.existsByTeamName(request.getTeamName())) {
            throw new BadRequestException("Team đã tồn tại: " + request.getTeamName());
        }
        return teamRepository.save(Team.builder().teamName(request.getTeamName()).build());
    }

    private UserResponse mapToUserResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .teamId(u.getTeam() != null ? u.getTeam().getId() : null)
                .teamName(u.getTeam() != null ? u.getTeam().getTeamName() : null)
                .commissionRate(u.getCommissionRate())
                .bankName(u.getBankName())
                .bankAccount(u.getBankAccount())
                .status(u.getStatus().name())
                .build();
    }
}