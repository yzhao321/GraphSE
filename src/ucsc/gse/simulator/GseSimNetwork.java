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
    // Variation in heterogeneous environments
    int simNetNodeNum = GSE_SIM_NETWORK_NODE_NUM;

    /* ****************************** Default value ****************************** */
    // Pastry node
    public static final int GSE_SIM_NETWORK_NODE_NUM = 1;
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
    public void simNetSetNodeNum(int nodeNum) {
        simNetNodeNum = nodeNum;
    }

    public void simNetSetIpAddress(String address) {
        try {
            simNetIpAddress = InetAddress.getByName(address);
        } catch (Exception e) {
            System.out.println("Gse sim set network address error: " + e);
        }
    }

    public int simNetGetNodeNum() {
        return simNetNodeNum;
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
