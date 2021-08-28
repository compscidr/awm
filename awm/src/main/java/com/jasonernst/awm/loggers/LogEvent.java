package com.jasonernst.awm.loggers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogEvent {

    public enum EventType {
        SUCCESS,
        FAILURE,
        MALFORMED
    }

    public enum LogType {
        DISK,
        NETWORK,
        DB
    }

    private EventType eventType;
    private LogType logType;
    private int numRecords;
}
