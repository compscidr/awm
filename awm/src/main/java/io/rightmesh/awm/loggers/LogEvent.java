package io.rightmesh.awm.loggers;

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

    EventType etype;
    LogType ltype;
    int numRecords;

    public LogEvent(EventType etype, LogType ltype) {
        this.etype = etype;
        this.numRecords = 0;
    }

    public LogEvent(EventType etype, LogType ltype, int numRecords) {
        this.etype = etype;
        this.ltype = ltype;
        this.numRecords = numRecords;
    }

    public EventType getEventType() {
        return etype;
    }

    public LogType getLogType() { return ltype; }

    public int getNumRecords() { return numRecords; }
}
