package ru.hookaorder.backend.feature.comment.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.hookaorder.backend.feature.BaseEntity;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "comment")
@Data
public class CommentEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn
    @JsonProperty(value = "owner_id")
    private UserEntity ownerId;
    @ManyToOne
    @JoinColumn
    @JsonProperty(value = "place_id")
    private PlaceEntity placeId;
    @ManyToOne
    @JoinColumn
    @JsonProperty(value = "user_id")
    private UserEntity userId;
    @Column(name = "comment", nullable = false)
    private String comment;
    @Column(name = "is_publish")
    @JsonProperty(value = "is_publish")
    private Boolean isPublish = true;
}
