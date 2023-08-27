/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.simulator;

import java.net.InetAddress;

public class GseSimNetwork {
    // Network config (Use a same [address, bootport] join a same Pastry group)
    InetAddress simNetIpAddress = null;
    int simNetBootPort = GSE_SIM_NETWORK_BOOT_PORT;
    int simNetBindPort = GSE_SIM_NETWORK_BIND_PORT;

    /* ****************************** Default value ****************************** */
    // Network ports
    public static final int GSE_SIM_NETWORK_BIND_PORT = 10000;
    public static final int GSE_SIM_NETWORK_BOOT_PORT = 10000;

    public GseSimNetwork() {
        try {
            simNetIpAddress = InetAddress.getLocalHost();
        } catch (Exception e) {
            System.out.println("Gse sim network address error: " + e);
        }
    }

    /* ****************************** Interface for set/get ********************** */
    public void simNetSetIpAddress(String address) {
        try {
            simNetIpAddress = InetAddress.getByName(address);
        } catch (Exception e) {
            System.out.println("Gse sim set network address error: " + e);
        }
    }

    public InetAddress simNetGetIpAddress() {
        return simNetIpAddress;
    }

    public int simNetGetBootPort() {
        return simNetBootPort;
    }

    public int simNetGetBindPort() {
        return simNetBindPort;
    }
}
