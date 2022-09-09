package ru.hookaorder.backend.feature.comment.repository;

import org.springframework.data.repository.CrudRepository;
import ru.hookaorder.backend.feature.comment.entity.CommentEntity;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends CrudRepository<CommentEntity, Long> {
    Optional<CommentEntity>findById(Long id);

    List<CommentEntity> findAll();

    void deleteById(Long Id);

    CommentEntity save(CommentEntity commentEntity);
}
