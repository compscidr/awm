package com.jasonernst.awm.stats;

public class BatteryStats {
    private float battery_percent;

    public BatteryStats(float battery_percent) {
        this.battery_percent = battery_percent;
    }

    public float getBatteryPercent() {
        return battery_percent;
    }
}
