package ru.hookaorder.backend.feature.comment.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import ru.hookaorder.backend.feature.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@Table(name = "comments")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE comments set deleted_at = now()::timestamp where id=?")
public class CommentEntity extends BaseEntity {

    @Column(name = "text")
    @JsonProperty(value = "text")
    @Size(max = 255, message = "Комментарий слишкой длинный")
    private String text;
}
