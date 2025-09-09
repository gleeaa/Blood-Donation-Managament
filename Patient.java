package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends User {
    private String requiredBloodType;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<BloodRequest> requestHistory;
}