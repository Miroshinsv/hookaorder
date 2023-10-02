package ru.hookaorder.backend.feature.place.exception;

public class PlaceNotCreatedException extends RuntimeException {
    public PlaceNotCreatedException(String message) {
        super(message);
    }
}
