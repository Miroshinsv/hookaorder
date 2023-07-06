package ru.hookaorder.backend.feature.subscribe;

public enum EPlaceNotifyMode {
  SUBSCRIBERS("SUBSCRIBERS"),
  STAFF("STAFF"),
  ALL("ALL");

  private final String value;

  EPlaceNotifyMode(String value) {
    this.value = value;
  }
}
