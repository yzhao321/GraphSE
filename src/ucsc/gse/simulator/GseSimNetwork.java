/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.simulator;

public class GseSimNetwork {
    int simNetNodeNum = GSE_SIM_NETWORK_NODE_NUM;
    String simNetIpAddress = GSE_SIM_NETWORK_IP_ADDR;
    int simNetBindPort = GSE_SIM_NETWORK_BIND_PORT;
    int simNetBootPort = GSE_SIM_NETWORK_BOOT_PORT;

    /* ****************************** Default value ****************************** */
    // Pastry node
    public static final int GSE_SIM_NETWORK_NODE_NUM = 10;
    // Network address and ports
    public static final String GSE_SIM_NETWORK_IP_ADDR = "10.0.0.187";
    public static final int GSE_SIM_NETWORK_BIND_PORT = 9050;
    public static final int GSE_SIM_NETWORK_BOOT_PORT = 9050;

    /* ****************************** Interface for set/get ********************** */
    public void simNetSetNodeNum(int nodeNum) {
        simNetNodeNum = nodeNum;
    }

    public int simNetGetNodeNum() {
        return simNetNodeNum;
    }

    public String simNetGetIpAddress() {
        return simNetIpAddress;
    }

    public int simNetGetBootPort() {
        return simNetBootPort;
    }

    public int simNetGetBindPort() {
        return simNetBindPort;
    }
}
