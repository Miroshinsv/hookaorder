package ru.hookaorder.backend.feature.place.exception;

public class PlaceImageNotUploadedException extends RuntimeException{
    public PlaceImageNotUploadedException(String message) {
        super(message);
    }
}
