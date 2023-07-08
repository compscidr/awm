package com.jasonernst.awm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import android.content.Context;
import android.net.ConnectivityManager;

import androidx.test.platform.app.InstrumentationRegistry;

import com.jasonernst.awm.loggers.NetworkLogger;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ConnectivityManager manager = Mockito.mock(ConnectivityManager.class);
        doReturn(manager).when(context).getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkLogger networkLogger = new NetworkLogger(context, false, false, "localhost");

        networkLogger.isWifiConnected();
        networkLogger.isMobileConnected();
        networkLogger.isWifiIsMobileConnected();
    }
}