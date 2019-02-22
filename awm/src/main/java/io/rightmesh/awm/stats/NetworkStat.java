package io.rightmesh.awm.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class NetworkStat {

    String name;
    String mac;
    Set<String> macs = new HashSet<>();
    Map<String, String> mac_names = new HashMap<>();

    public int getSize() {
        return macs.size();
    }

    public String getMac() {
        return mac;
    }

    public String getName() { return name; }

    public Set<String> getMacs() {
        return macs;
    }

    public Map<String, String> getMacNames() {
        return mac_names;
    }
}
