package io.rightmesh.awm.loggers;

public class LogEvent {

    enum EventType {
        SUCCESS,
        FAILURE,
        MALFORMED
    }

    EventType type;

    public LogEvent(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }
}
