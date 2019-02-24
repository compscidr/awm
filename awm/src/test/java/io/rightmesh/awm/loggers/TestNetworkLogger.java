package io.rightmesh.awm.loggers;

import android.content.Context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

@ExtendWith(RxJavaTestExtension.class)
public class TestNetworkLogger {

    @Mock Context context;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Test sending a single network stat in a single network record")
    public void sendSingleStatinSingleRecord() {
        NetworkLogger networkLogger = new NetworkLogger(context);

        ArrayList<String> disklog = new ArrayList<>();
        disklog.add("{\t\"awm_measure\": {\t\t\"reporting_device\": {\t\t\t\"uuid\": \"8bc1e828-e0b4-4132-8f7f-36dddd279f10\",\t\t\t\"ipv4_address\": \"0.0.0.0\",\t\t\t\"ipv6_address\": \"0:0:0:0:0:0:0:0\",\t\t\t\"timestamp\": \"2019-02-23 11:54:09.019\",\t\t\t\"longitude\": \"-58.4355621\",\t\t\t\"latitude\": \"54.1800128\",\t\t\t\"bt_mac_address\": \"null\",\t\t\t\"wifi_mac_address\": \"null\",\t\t\t\"OS\": \"jupiter-test\",\t\t\t\"battery_life\": \"100.0\",\t\t\t\"has_cellular_internet\": \"false\",\t\t\t\"has_wifi_internet\": \"false\",\t\t\t\"cellular_throughput\": \"0.0\",\t\t\t\"wifi_throughput\": \"0.0\",\t\t\t\"cellular_ping\": \"0\",\t\t\t\"wifi_ping\": \"0\",\t\t\t\"cellular_operator\": \"unknown\",\t\t\t\"cellular_network_type\": \"0\"\t\t},\t\t\"devices\": [\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname\"\t\t\t}\t\t]\t}}\n");

        networkLogger.uploadPendingLogsThread(disklog);
    }

    @Test
    @DisplayName("Test sending a two network stats in a single network record")
    public void sendMultipleStatsinSingleRecord() {
        NetworkLogger networkLogger = new NetworkLogger(context);

        ArrayList<String> disklog = new ArrayList<>();
        disklog.add("{\t\"awm_measure\": {\t\t\"reporting_device\": {\t\t\t\"uuid\": \"8bc1e828-e0b4-4132-8f7f-36dddd279f10\",\t\t\t\"ipv4_address\": \"0.0.0.0\",\t\t\t\"ipv6_address\": \"0:0:0:0:0:0:0:0\",\t\t\t\"timestamp\": \"2019-02-23 11:54:30.556\",\t\t\t\"longitude\": \"-58.4355621\",\t\t\t\"latitude\": \"54.1800128\",\t\t\t\"bt_mac_address\": \"null\",\t\t\t\"wifi_mac_address\": \"null\",\t\t\t\"OS\": \"jupiter-test\",\t\t\t\"battery_life\": \"100.0\",\t\t\t\"has_cellular_internet\": \"false\",\t\t\t\"has_wifi_internet\": \"false\",\t\t\t\"cellular_throughput\": \"0.0\",\t\t\t\"wifi_throughput\": \"0.0\",\t\t\t\"cellular_ping\": \"0\",\t\t\t\"wifi_ping\": \"0\",\t\t\t\"cellular_operator\": \"unknown\",\t\t\t\"cellular_network_type\": \"0\"\t\t},\t\t\"devices\": [\t\t\t{\t\t\t\t\"mac_address\": \"22:33:44:55:66:77\",\t\t\t\t\"signal_strength\": -55,\t\t\t\t\"frequency\": 2432,\t\t\t\t\"channel_width\": 40,\t\t\t\t\"security\": \"wep\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname2\"\t\t\t},\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname\"\t\t\t}\t\t]\t}}\n");

        networkLogger.uploadPendingLogsThread(disklog);
    }

    @Test
    @DisplayName("Test sending a two network stats in a two network records")
    public void sendMultipleStatsinMultipleRecords() {
        NetworkLogger networkLogger = new NetworkLogger(context);

        ArrayList<String> disklog = new ArrayList<>();
        disklog.add("{\t\"awm_measure\": {\t\t\"reporting_device\": {\t\t\t\"uuid\": \"8bc1e828-e0b4-4132-8f7f-36dddd279f10\",\t\t\t\"ipv4_address\": \"0.0.0.0\",\t\t\t\"ipv6_address\": \"0:0:0:0:0:0:0:0\",\t\t\t\"timestamp\": \"2019-02-23 11:54:09.019\",\t\t\t\"longitude\": \"-58.4355621\",\t\t\t\"latitude\": \"54.1800128\",\t\t\t\"bt_mac_address\": \"null\",\t\t\t\"wifi_mac_address\": \"null\",\t\t\t\"OS\": \"jupiter-test\",\t\t\t\"battery_life\": \"100.0\",\t\t\t\"has_cellular_internet\": \"false\",\t\t\t\"has_wifi_internet\": \"false\",\t\t\t\"cellular_throughput\": \"0.0\",\t\t\t\"wifi_throughput\": \"0.0\",\t\t\t\"cellular_ping\": \"0\",\t\t\t\"wifi_ping\": \"0\",\t\t\t\"cellular_operator\": \"unknown\",\t\t\t\"cellular_network_type\": \"0\"\t\t},\t\t\"devices\": [\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname\"\t\t\t}\t\t]\t}}\n");
        disklog.add("{\t\"awm_measure\": {\t\t\"reporting_device\": {\t\t\t\"uuid\": \"8bc1e828-e0b4-4132-8f7f-36dddd279f10\",\t\t\t\"ipv4_address\": \"0.0.0.0\",\t\t\t\"ipv6_address\": \"0:0:0:0:0:0:0:0\",\t\t\t\"timestamp\": \"2019-02-23 11:57:43.217\",\t\t\t\"longitude\": \"-58.4355621\",\t\t\t\"latitude\": \"54.1800128\",\t\t\t\"bt_mac_address\": \"null\",\t\t\t\"wifi_mac_address\": \"null\",\t\t\t\"OS\": \"jupiter-test\",\t\t\t\"battery_life\": \"100.0\",\t\t\t\"has_cellular_internet\": \"false\",\t\t\t\"has_wifi_internet\": \"false\",\t\t\t\"cellular_throughput\": \"0.0\",\t\t\t\"wifi_throughput\": \"0.0\",\t\t\t\"cellular_ping\": \"0\",\t\t\t\"wifi_ping\": \"0\",\t\t\t\"cellular_operator\": \"unknown\",\t\t\t\"cellular_network_type\": \"0\"\t\t},\t\t\"devices\": [\t\t\t{\t\t\t\t\"mac_address\": \"22:33:44:55:66:77\",\t\t\t\t\"signal_strength\": -55,\t\t\t\t\"frequency\": 2432,\t\t\t\t\"channel_width\": 40,\t\t\t\t\"security\": \"wep\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname2\"\t\t\t}\t\t]\t}}\n");
        disklog.add("{\t\"awm_measure\": {\t\t\"reporting_device\": {\t\t\t\"uuid\": \"8bc1e828-e0b4-4132-8f7f-36dddd279f10\",\t\t\t\"ipv4_address\": \"0.0.0.0\",\t\t\t\"ipv6_address\": \"0:0:0:0:0:0:0:0\",\t\t\t\"timestamp\": \"2019-02-23 12:14:46.292\",\t\t\t\"longitude\": \"-58.4355621\",\t\t\t\"latitude\": \"54.1800128\",\t\t\t\"bt_mac_address\": \"null\",\t\t\t\"wifi_mac_address\": \"null\",\t\t\t\"OS\": \"jupiter-test\",\t\t\t\"battery_life\": \"100.0\",\t\t\t\"has_cellular_internet\": \"false\",\t\t\t\"has_wifi_internet\": \"false\",\t\t\t\"cellular_throughput\": \"0.0\",\t\t\t\"wifi_throughput\": \"0.0\",\t\t\t\"cellular_ping\": \"0\",\t\t\t\"wifi_ping\": \"0\",\t\t\t\"cellular_operator\": \"unknown\",\t\t\t\"cellular_network_type\": \"0\"\t\t},\t\t\"devices\": [\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname9\"\t\t\t},\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname3\"\t\t\t},\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname\"\t\t\t},\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname7\"\t\t\t},\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname4\"\t\t\t},\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname5\"\t\t\t},\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname6\"\t\t\t},\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname8\"\t\t\t},\t\t\t{\t\t\t\t\"mac_address\": \"00:11:22:33:44:55\",\t\t\t\t\"signal_strength\": -45,\t\t\t\t\"frequency\": 2412,\t\t\t\t\"channel_width\": 20,\t\t\t\t\"security\": \"wpa2\",\t\t\t\t\"mac_type\": 1,\t\t\t\t\"network_name\": \"testname10\"\t\t\t}\t\t]\t}}\n");

        networkLogger.uploadPendingLogsThread(disklog);
    }
}
