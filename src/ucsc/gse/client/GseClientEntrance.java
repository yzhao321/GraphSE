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
    // GraphSE simulator for deploying the assumed configuration
    static GseSim clientSimulator = new GseSim();
    // GraphSE launch mode
    static boolean ifLaunchWithShell = false;
    // GraphSE default computation launch
    static String[] clientComputation = {GseSimComputation.GSE_COMP_TOPIC_CC};

    public static void main(String[] args) throws Exception {
        // Get the config from command args
        processArgs(args);

        // Launch node only
        if (!ifLaunchWithShell) {
            for (String compStr : clientComputation) {
                clientSimulator.simAddComputation(compStr);
            }

            clientSimulator.runSim();
            return;
        }

        // Running with a visualized shell
        System.out.println("\n=================================================================");
        System.out.println("-----------------------------GraphSE-----------------------------");
        System.out.println("=================================================================\n");
        // Launch a shell client for developer
        GseClientShell clientShell = new GseClientShell(clientSimulator);
        clientShell.runShell();
    }

    // Input args
    static final int GSE_CLIENT_ENTRANCE_INPUT_ARG_SHELL = 0;
    static final int GSE_CLIENT_ENTRANCE_INPUT_ARG_IP = 1;
    // Args list: [Launching Mode], [Network Address]
    private static void processArgs(String[] args) {
        // Select the launch mode (Cmd Shell Control Mode or Direct Mode)
        if (args.length == 0) {
            ifLaunchWithShell = true;
        } else if (args.length > GSE_CLIENT_ENTRANCE_INPUT_ARG_SHELL) {
            ifLaunchWithShell = args[GSE_CLIENT_ENTRANCE_INPUT_ARG_SHELL].equals("Shell") ? true : false;
        }

        // Config the network if it is given in the args, which is required in a distributed environment to boot a same address and port
        if (args.length > GSE_CLIENT_ENTRANCE_INPUT_ARG_IP) {
            clientSimulator.simSetNetworkAddress(args[GSE_CLIENT_ENTRANCE_INPUT_ARG_IP]);
        }
    }
}
