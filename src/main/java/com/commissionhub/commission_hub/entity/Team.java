package com.commissionhub.commission_hub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity @Table(name = "teams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Team extends BaseEntity {

    @Column(name = "team_name", nullable = false, unique = true, length = 100)
    private String teamName;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<User> users;
}