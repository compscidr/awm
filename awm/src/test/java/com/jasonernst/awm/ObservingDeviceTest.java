package com.jasonernst.awm;

import com.jasonernst.awm_common.stats.ReportingDevice;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ObservingDeviceTest {
    @Test public void prepareJsonTest() {
        UUID uuid = UUID.randomUUID();
        ReportingDevice observingDevice = new ReportingDevice(uuid, "TestOS");
        Timestamp now = new Timestamp(new Date().getTime());
        String json = observingDevice.prepareJSON("", now);
    }
}
