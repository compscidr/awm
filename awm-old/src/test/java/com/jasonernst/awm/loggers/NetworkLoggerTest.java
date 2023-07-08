package com.jasonernst.awm.loggers;

import static org.mockito.Mockito.doReturn;

import android.content.Context;
import android.net.ConnectivityManager;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NetworkLoggerTest {

    @Test public void NetworkLoggerTest() {
        Context context = Mockito.mock(Context.class);
        ConnectivityManager manager = Mockito.mock(ConnectivityManager.class);
        doReturn(manager).when(context).getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkLogger networkLogger = new NetworkLogger(context, false, false, "localhost");

        networkLogger.isWifiConnected();
        networkLogger.isMobileConnected();
        networkLogger.isWifiIsMobileConnected();
    }
}
