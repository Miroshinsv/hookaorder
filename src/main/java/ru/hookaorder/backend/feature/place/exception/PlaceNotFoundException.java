package ru.hookaorder.backend.feature.place.exception;

public class PlaceNotFoundException extends RuntimeException{
    public PlaceNotFoundException(String message) {
        super(message);
    }
}
