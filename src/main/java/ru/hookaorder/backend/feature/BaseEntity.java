package ru.hookaorder.backend.feature;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Inheritance
@Data
@MappedSuperclass
@JsonIgnoreProperties(value = "deleted_at")
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "created_at", updatable = false)
    @JsonProperty(value = "created_at")
    protected LocalDate createdAt;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "updated_at")
    @JsonProperty(value = "updated_at")
    protected LocalDate updatedAt;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "deleted_at")
    @JsonProperty(value = "deleted_at")
    protected LocalDate deletedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

//    @JsonIgnore
//    public LocalDate getCreatedAt() {
//        return createdAt;
//    }
//    @JsonIgnore
//    public void setCreatedAt(LocalDate createdAt) {
//        this.createdAt = createdAt;
//    }
//    @JsonIgnore
//    public LocalDate getUpdatedAt() {
//        return updatedAt;
//    }
//    @JsonIgnore
//    public void setUpdatedAt(LocalDate updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//    @JsonIgnore
//    public LocalDate getDeletedAt() {
//        return deletedAt;
//    }
//    @JsonIgnore
//    public void setDeletedAt(LocalDate deletedAt) {
//        this.deletedAt = deletedAt;
//    }
}
