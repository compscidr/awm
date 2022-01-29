package com.jasonernst.awm_common.stats;

import com.google.gson.Gson;

import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NetworkStat {
    private ReportingDevice reportingDevice;
    private ConcurrentHashMap<String, ObservedDevice> devices;

    public String toJSON() throws IllegalArgumentException {
        if (reportingDevice.getPosition().latitude == 0 || reportingDevice.getPosition().longitude == 0) {
            throw new IllegalArgumentException("null position. ignoring this measure");
        }
        return new Gson().toJson(this);
    }

    public static NetworkStat fromJSON(String json) {
        return new Gson().fromJson(json, NetworkStat.class);
    }
}