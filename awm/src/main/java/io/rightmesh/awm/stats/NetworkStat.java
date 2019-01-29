package io.rightmesh.awm;

import java.util.HashSet;
import java.util.Set;

public abstract class NetworkStat {

    String mac;
    Set<String> macs = new HashSet<>();

    public int getSize() {
        return macs.size();
    }

    public String getMac() {
        return mac;
    }

    public Set<String> getMacs() {
        return macs;
    }
}
