package com.jasonernst.awm;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ObservingDeviceTest {
    @Test public void prepareJsonTest() {
        UUID uuid = UUID.randomUUID();
        ReportingDevice reportingDevice = new ReportingDevice(uuid, "TestOS");
        Timestamp now = new Timestamp(new Date().getTime());
        String json = reportingDevice.prepareJSON("", now);
    }
}
