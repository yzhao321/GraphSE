/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.client;

import ucsc.gse.simulator.GseSim;
import ucsc.gse.simulator.GseSimComputation;

public class GseClientEntrance {

    // Input args
    static final int GSE_CLIENT_ENTRANCE_INPUT_ARG_SHELL = 0;
    static final int GSE_CLIENT_ENTRANCE_INPUT_ARG_IP = 1;

    // Default computation launch
    static String[] clientComputation = {GseSimComputation.GSE_COMP_TOPIC_CC};

    public static void main(String[] args) throws Exception {
        // GraphSE simulator for deploying the assumed configuration
        GseSim clientSimulator = new GseSim();

        // Select the launch mode (Cmd Shell Control Mode or Direct Mode)
        boolean ifLaunchWithShell = false;
        if (args.length == 0) {
            ifLaunchWithShell = true;
        } else if (args.length > GSE_CLIENT_ENTRANCE_INPUT_ARG_SHELL) {
            ifLaunchWithShell = args[GSE_CLIENT_ENTRANCE_INPUT_ARG_SHELL] == "Shell" ? true : false;
        }

        // Config the network if it is given in the args, which is required in a distributed environment to boot a same address and port
        if (args.length > GSE_CLIENT_ENTRANCE_INPUT_ARG_IP) {
            clientSimulator.simSetNetworkAddress(args[GSE_CLIENT_ENTRANCE_INPUT_ARG_IP]);
        }

        if (ifLaunchWithShell) {
            System.out.println("\n=================================================================");
            System.out.println("-----------------------------GraphSE-----------------------------");
            System.out.println("=================================================================\n");

            // Launch a shell client for developer
            GseClientShell clientShell = new GseClientShell(clientSimulator);
            clientShell.start();
        } else {
            for (String compStr : clientComputation) {
                clientSimulator.simAddComputation(compStr);
            }

            // Do not use thread start
            clientSimulator.run();
            clientSimulator.simLaunchComputation();
        }

    }
}
