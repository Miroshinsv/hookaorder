package ru.hookaorder.backend.feature.place.exception;

public class PlaceAccessDeniedException extends RuntimeException{
    public PlaceAccessDeniedException(String message) {
        super(message);
    }
}
