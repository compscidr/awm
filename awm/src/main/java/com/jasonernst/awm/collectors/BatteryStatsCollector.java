package com.jasonernst.awm.collectors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.jasonernst.awm.stats.BatteryStats;

public class BatteryStatsCollector extends StatsCollector {

    private static final String TAG = BatteryStatsCollector.class.getCanonicalName();
    private PowerConnectionReceiver powerConnectionReceiver;
    private Context context;
    private volatile boolean started = false;

    public BatteryStatsCollector(Context context) {
        this.context = context;
        powerConnectionReceiver = new PowerConnectionReceiver();
    }

    @Override
    public void start() throws Exception {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(powerConnectionReceiver, ifilter);
        started = true;
    }

    @Override
    public void stop() {
        if(powerConnectionReceiver != null && started) {
            context.unregisterReceiver(powerConnectionReceiver);
        }
        started = false;
    }

    public class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = (level / (float)scale) * 100;

            Log.d(TAG, "BATTERY LEVEL: " + batteryPct + "%");
            eventBus.post(new BatteryStats(batteryPct));
        }
    }
}
