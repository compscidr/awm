package com.jasonernst.awm;

import com.google.gson.Gson;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ObservingDeviceTest {
    @Test public void prepareJsonTest() {
        UUID uuid = UUID.randomUUID();
        ReportingDevice reportingDevice = new ReportingDevice(uuid, "TestOS");
        Timestamp now = new Timestamp(new Date().getTime());
        String json = new Gson().toJson(reportingDevice);
        assert(json.contains("TestOS"));
    }
}
