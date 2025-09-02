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

Patient.java
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