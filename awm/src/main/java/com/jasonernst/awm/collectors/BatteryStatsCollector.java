package com.jasonernst.awm.collectors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonernst.awm.stats.BatteryStats;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BatteryStatsCollector extends StatsCollector {

    private final Logger logger = LoggerFactory.getLogger(BatteryStatsCollector.class);
    private static final String TAG = BatteryStatsCollector.class.getCanonicalName();
    private PowerConnectionReceiver powerConnectionReceiver;
    private Context context;
    private volatile boolean started = false;

    public BatteryStatsCollector(Context context) {
        this.context = context;
        powerConnectionReceiver = new PowerConnectionReceiver();
    }

    @Override
    public void start() {
        if (!started) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            context.registerReceiver(powerConnectionReceiver, ifilter);
            started = true;
            logger.info("Starting {}", BatteryStatsCollector.class);
        } else {
            logger.warn("Already started {}", BatteryStatsCollector.class);
        }
    }

    @Override
    public void stop() {
        if(powerConnectionReceiver != null && started) {
            context.unregisterReceiver(powerConnectionReceiver);
            logger.info("Stopped {}", BatteryStatsCollector.class);
        } else {
            logger.warn("Already stopped {}", BatteryStatsCollector.class);
        }
        started = false;
    }

    public class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = (level / (float)scale) * 100;
            logger.info("Battery level: {}%", batteryPct);
            eventBus.post(new BatteryStats(batteryPct));
        }
    }
}
