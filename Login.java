package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Login {
    @Id
    private String username;

    private String password;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
