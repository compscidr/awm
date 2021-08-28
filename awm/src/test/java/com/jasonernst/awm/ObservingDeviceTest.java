package com.jasonernst.awm;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ObservingDeviceTest {
    @Test public void prepareJsonTest() {
        UUID uuid = UUID.randomUUID();
        ObservingDevice observingDevice = new ObservingDevice(uuid, "TestOS");
        Timestamp now = new Timestamp(new Date().getTime());
        String json = observingDevice.prepareJSON("", now);
    }
}
