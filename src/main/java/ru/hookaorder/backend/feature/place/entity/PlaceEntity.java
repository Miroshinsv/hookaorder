package ru.hookaorder.backend.feature.place.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import ru.hookaorder.backend.feature.address.entity.AddressEntity;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * Place entity
 */
@Entity
@Table(name = "places")
@Data
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Name place.
     * Can't be nullable
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Time when place open
     */
    @Pattern(regexp = "^([01][0-9]|2[0-3]):([0-5][0-9])$")
    @Column(name = "start_time")
    private String startTime;

    /**
     * Time when place close
     */
    @Column(name = "end_time")
    @Pattern(regexp = "^([01][0-9]|2[0-3]):([0-5][0-9])$")
    private String endTime;

    /**
     * Url place logo
     */
    @Column(name = "logo_url")
    @URL(regexp = "^(http|https).*")
    private String logoUrl;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    @JsonProperty(value = "address")
    private AddressEntity address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Ignore delete on return entity
     *
     * @return LocalDateTime
     */
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
