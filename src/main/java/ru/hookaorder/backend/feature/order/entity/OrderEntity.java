package ru.hookaorder.backend.feature.order.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.hookaorder.backend.feature.BaseEntity;
import ru.hookaorder.backend.feature.comment.entity.CommentEntity;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.user.entity.UserEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "orders")
@Setter
@Getter
@EqualsAndHashCode
public class OrderEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "places_id", referencedColumnName = "id", nullable = false)
    @JsonProperty(value = "place_id")
    private PlaceEntity placeId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonProperty(value = "user_id")
    private UserEntity userId;

    @Column(name = "order_time", nullable = false)
    @JsonProperty(value = "order_time")
    private String orderTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonProperty(value = "comment", access = JsonProperty.Access.READ_ONLY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private CommentEntity comment;
}
