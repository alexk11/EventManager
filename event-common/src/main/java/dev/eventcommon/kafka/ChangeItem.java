package dev.eventcommon.kafka;

public record ChangeItem(
    String field,
    Object oldValue,
    Object newValue) {
}
