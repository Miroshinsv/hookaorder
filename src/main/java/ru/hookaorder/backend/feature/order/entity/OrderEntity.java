package ru.hookaorder.backend.feature.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "places_id", referencedColumnName = "id", nullable = false)
    private PlaceEntity placeId;

//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
//    private Long userId;

    @Column(name = "order_time", nullable = false)
    private String orderTime;

    @Column(name = "comment")
    private String comment ;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonIgnore
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
