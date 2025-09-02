package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donor extends User {
    private String bloodType;
    private String location;
    private boolean availability;

    // Donation history table can be linked later
}
