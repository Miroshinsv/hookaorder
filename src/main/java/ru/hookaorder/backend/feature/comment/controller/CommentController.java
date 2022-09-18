package ru.hookaorder.backend.feature.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.comment.entity.CommentEntity;
import ru.hookaorder.backend.feature.comment.repository.CommentRepository;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/comment")
public class CommentController{

    private final CommentRepository commentRepository;

    @GetMapping("/get/{id}")
    ResponseEntity<CommentEntity> getCommentById(@PathVariable Long id){
        return commentRepository.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("get/all")
    ResponseEntity<List<CommentEntity>> getAllComment(){
        return ResponseEntity.ok(commentRepository.findAll());
    }

    @PostMapping("/create")
    ResponseEntity<CommentEntity> createComment(@RequestBody CommentEntity commentEntity){
        if (commentEntity.getPlaceId().getId() != null && commentEntity.getOwnerId().getId() != null && commentEntity.getUserId().getId() != null) {
            if (commentEntity.getPlaceId().getId() == null || commentEntity.getOwnerId().getId() == null || commentEntity.getUserId().getId() != null)
                    return ResponseEntity.ok(commentRepository.save(commentEntity));
            if (commentEntity.getOwnerId().getId() != null || commentEntity.getUserId().getId() == null)
                    return ResponseEntity.ok(commentRepository.save(commentEntity));
            if (commentEntity.getPlaceId().getId() != null)
                return ResponseEntity.ok(commentRepository.save(commentEntity));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/update/{id}")
    ResponseEntity<CommentEntity> updateComment(@PathVariable Long id, @RequestBody CommentEntity commentEntity){
        return commentRepository
                .findById(id)
                .map((val) ->
                {
                    commentEntity.setId(id);
                    return ResponseEntity.ok(commentRepository.save(commentEntity));})
                .orElse(ResponseEntity.badRequest().build());
    }
}
