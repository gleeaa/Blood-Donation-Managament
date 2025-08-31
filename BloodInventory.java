package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String bloodType;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;
}
