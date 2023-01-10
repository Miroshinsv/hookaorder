package ru.hookaorder.backend.feature.rating.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.hookaorder.backend.feature.BaseEntity;
import ru.hookaorder.backend.feature.comment.entity.CommentEntity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.hookaorder.backend.config.Vars.MAX_RATING_VALUE;
import static ru.hookaorder.backend.config.Vars.MIN_RATING_VALUE;

@Entity
@Table(name = "ratings")
@Getter
@Setter
public class RatingEntity extends BaseEntity {

    @Column(name = "rating_value")
    @JsonProperty(value = "rating_value")
    @Min(MIN_RATING_VALUE)
    @Max(MAX_RATING_VALUE)
    private byte ratingValue;

    @Column(name = "is_moderated")
    @JsonProperty(value = "is_enabled", access = JsonProperty.Access.READ_ONLY)
    private boolean isModerated;

    @Column(name="owner_id")
    @JsonProperty(value = "owner_id", access = JsonProperty.Access.READ_ONLY)
    private Long ownerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private CommentEntity comment;

}
