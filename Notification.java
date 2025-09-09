package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationID;

    private int recipientID; // could link to User
    private String message;
    private String status; // Sent, Read, Unread
}
