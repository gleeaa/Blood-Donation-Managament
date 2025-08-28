package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int requestId;

    private String requestedBloodType;
    private String status; // Pending, Fulfilled, Rejected

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
}
