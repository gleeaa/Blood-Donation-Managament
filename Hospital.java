package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hospital extends User {
    private String hospitalName;
    private String location;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<BloodInventory> inventory;
}
