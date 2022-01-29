package com.jasonernst.awm_server;

import com.jasonernst.awm_common.stats.NetworkStat;
import com.jasonernst.awm_common.stats.ObservedDevice;
import com.jasonernst.awm_common.stats.ReportingDevice;

import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlPersistence implements Persistence {

    private String host;
    private String user;
    private String password;
    private int port;
    private Connection _connection;

    public MySqlPersistence(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Override
    public boolean connect() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            String url = host + ":" + port;
            _connection = DriverManager.getConnection(url, user, password);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ObservedDevice> getObservedDevicesByBoundingBox(Rectangle2D rectangle2D) {
        return null;
    }

    @Override
    public List<ObservedDevice> getObservedWifiDevicesByBoundingBox(Rectangle2D rectangle2D) {
        return null;
    }

    @Override
    public List<ObservedDevice> getObservedBluetoothDevicesByBoundingBox(Rectangle2D rectangle2D) {
        return null;
    }

    @Override
    public List<NetworkStat> saveNetworkStats(List<NetworkStat> stats) {
        for (NetworkStat stat : stats) {
            // insert the device which made the observation
            ReportingDevice reportingDevice = stat.getReportingDevice();
            String sql = "INSERT into `reporting_device` (`uuid`, `bt_mac_address`, " +
                    "`wifi_mac_address`, `ipv4_address`, `ipv6_address`, `timestamp`, " +
                    "`longitude`, `latitude`, `OS`, `battery_life`, `has_cellular_internet`, " +
                    "`has_wifi_internet`, `cellular_throughput`, `wifi_throughput`, " +
                    "`cellular_ping`, `wifi_ping`, `cellular_operator`, `cellular_network_type`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                PreparedStatement statement = _connection.prepareStatement(sql);
                statement.setString(1, reportingDevice.getUuid().toString());
                statement.setString( 2, reportingDevice.getBtMac());
                statement.setString(3, reportingDevice.getWifiMac());
                statement.setInt(4, ByteBuffer.wrap(reportingDevice.getInet4Address().getAddress()).getInt());
                statement.setBytes (5, reportingDevice.getInet6Address().getAddress());
                statement.setTimestamp(6, reportingDevice.getTimestamp());
                statement.setDouble(7, reportingDevice.getPosition().longitude);
                statement.setDouble(8, reportingDevice.getPosition().latitude);
                statement.setString(9, reportingDevice.getOS());
                statement.setFloat(10, reportingDevice.getBattery_life());
                statement.setBoolean(11, reportingDevice.isHasCellularInternet());
                statement.setBoolean(12, reportingDevice.isHasWiFiInternet());
                statement.setFloat(13, reportingDevice.getCellularThroughput());
                statement.setFloat(14, reportingDevice.getWifiThroughput());
                statement.setInt(15, reportingDevice.getCellularPing());
                statement.setInt(16, reportingDevice.getWifiPing());
                statement.setString(17, reportingDevice.getCellularOperator());
                statement.setInt(18, reportingDevice.getCellularNetworktype());
                int affectedRows = statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reportingDevice.setId(generatedKeys.getLong(1));
                }

                // insert all observed devices
                for (ObservedDevice observedDevice : stat.getDevices().values()) {
                    sql = "INSERT into `observed_device` (`reporting_device_id`, `mac_address`, " +
                            "`mac_type`, `network_name`, `signal_strength`, `frequency`, " +
                            "`channel_width`, `security`)";
                    statement = _connection.prepareStatement(sql);
                    statement.setLong(1, reportingDevice.getId());
                    statement.setString(2, observedDevice.getMac());
                    statement.setShort(3, observedDevice.getType().getValue());
                    statement.setString(4, observedDevice.getName());
                    statement.setInt(5, observedDevice.getSignalStrength());
                    statement.setInt(6, observedDevice.getFrequency());
                    statement.setInt(7, observedDevice.getChannelWidth());
                    statement.setString(8, observedDevice.getSecurity());
                    affectedRows = statement.executeUpdate();
                    generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        observedDevice.setId(generatedKeys.getLong(1));
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                return new ArrayList<>();
            }
        }
        return stats;
    }

    @Override
    public boolean disconnect() {
        try {
            _connection.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
