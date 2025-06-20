package com.example.librarymanagementsystem.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "blacklist")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistEntry {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String reason; // например: "Просрочка возврата книги"

    private long daysOverdue;

    private boolean resolved = false;

    private LocalDateTime addedAt;

    @PrePersist
    public void setAddedAtNow() {
        this.addedAt = LocalDateTime.now();
    }
}
