package ru.hookaorder.backend.feature.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hookaorder.backend.feature.user.exception.UserNotCreatedException;
import ru.hookaorder.backend.feature.user.exception.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>("User not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotCreatedException.class)
    public ResponseEntity<String> handleUserNotCreated(UserNotCreatedException ex) {
        return new ResponseEntity<>("User not created: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
