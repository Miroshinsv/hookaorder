package ru.hookaorder.backend.feature.image.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import ru.hookaorder.backend.feature.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE images set deleted_at = now()::timestamp where id=?")
public class ImageEntity extends BaseEntity {

    @Column(name = "url")
    private String url;
}
