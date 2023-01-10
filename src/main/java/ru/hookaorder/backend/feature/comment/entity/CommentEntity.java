package ru.hookaorder.backend.feature.comment.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.hookaorder.backend.feature.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@Table(name = "comments")
public class CommentEntity extends BaseEntity {

    @Column(name = "text")
    @JsonProperty(value = "text")
    @Size(max = 255, message = "Комментарий слишкой длинный")
    private String text;
}
