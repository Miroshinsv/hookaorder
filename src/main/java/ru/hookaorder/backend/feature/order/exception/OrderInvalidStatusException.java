package ru.hookaorder.backend.feature.order.exception;

public class OrderInvalidStatusException extends RuntimeException{
    public OrderInvalidStatusException(String message) {
        super(message);
    }
}
