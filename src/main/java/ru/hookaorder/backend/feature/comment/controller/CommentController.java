package ru.hookaorder.backend.feature.comment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.comment.entity.CommentEntity;
import ru.hookaorder.backend.feature.comment.repository.CommentRepository;

import java.util.List;

@RestController
@RequestMapping(value = "/comment")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;

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
        return ResponseEntity.ok(commentRepository.save(commentEntity));
    }

    @PutMapping("/update")
    ResponseEntity<CommentEntity> updateComment(@PathVariable Long id){
        return commentRepository.findById(id).map((val) -> ResponseEntity.ok(commentRepository.save(val)))
                .orElse(ResponseEntity.badRequest().build());
    }
}
