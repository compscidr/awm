package com.jasonernst.awm.stats;

import com.google.gson.Gson;
import com.jasonernst.awm.ReportingDevice;

import org.json.JSONException;

import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NetworkStat {
    private ReportingDevice reportingDevice;
    private ConcurrentHashMap<String, ObservedDevice> devices;

    public String toJSON() throws JSONException {

        if(reportingDevice.getPosition().latitude == 0 || reportingDevice.getPosition().longitude == 0) {
            throw new JSONException("null position. ignoring this measure");
        }

        return new Gson().toJson(this);
    }
}
