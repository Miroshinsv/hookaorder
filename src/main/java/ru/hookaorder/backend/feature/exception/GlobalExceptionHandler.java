package ru.hookaorder.backend.feature.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hookaorder.backend.feature.order.exception.OrderAccessDeniedException;
import ru.hookaorder.backend.feature.order.exception.OrderInvalidStatusException;
import ru.hookaorder.backend.feature.order.exception.OrderNotCreatedException;
import ru.hookaorder.backend.feature.order.exception.OrderNotFoundException;
import ru.hookaorder.backend.feature.place.exception.PlaceAccessDeniedException;
import ru.hookaorder.backend.feature.place.exception.PlaceNotCreatedException;
import ru.hookaorder.backend.feature.place.exception.PlaceNotFoundException;
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

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFound(OrderNotFoundException ex) {
        return new ResponseEntity<>("Order not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderNotCreatedException.class)
    public ResponseEntity<String> handleOrderNotCreated(OrderNotCreatedException ex) {
        return new ResponseEntity<>("Order not created: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderAccessDeniedException.class)
    public ResponseEntity<String> handleOrderAccessDenied(OrderAccessDeniedException ex) {
        return new ResponseEntity<>("Access to order denied: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderInvalidStatusException.class)
    public ResponseEntity<String> handleOrderInvalidStatus(OrderInvalidStatusException ex) {
        return new ResponseEntity<>("Invalid status for order: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<String> handlePlaceNotFound(PlaceNotFoundException ex) {
        return new ResponseEntity<>("Place not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PlaceNotCreatedException.class)
    public ResponseEntity<String> handlePlaceNotCreated(PlaceNotCreatedException ex) {
        return new ResponseEntity<>("Place not created: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PlaceAccessDeniedException.class)
    public ResponseEntity<String> handlePlaceAccessDenied(PlaceAccessDeniedException ex) {
        return new ResponseEntity<>("Access to place denied: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
