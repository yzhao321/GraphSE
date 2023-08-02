/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */


package ucsc.gse.client;

import java.util.Map;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import ucsc.gse.simulator.GseSim;

/*
    Shell client cmd list for developers to test, debug, and view.

    Adding testing function steps:
        1. Add testing function in this class;
        2. Map a cmd name to the function in cmdList;
        3. Provide a description for the cmd in cmdDescription.

    Calling testing function steps:
        1. ./run.sh to run the client;
        2. input "?" to get the descriptions;
        3. input cmd name with args.
 */

public class GseClientShellCmd {
    // Shell testing cmd management
    Map<String, Function<List<String>, Boolean>> cmdList = new HashMap<>(); // Map cmd name to function
    ArrayList<Map.Entry<String, String>> cmdDescription = new ArrayList<>(); // Map cmd name to description

    // Simulating experimental setup, input, and computation
    GseSim clientSimulator = new GseSim();

    public GseClientShellCmd() {
        // Map cmd name to function
        cmdList.put("?",                (args) -> cmdHelp(args));
        cmdList.put("net",              (args) -> cmdSetSimNetwork(args));
        cmdList.put("input",            (args) -> cmdSetInputFile(args));
        cmdList.put("init",             (args) -> cmdInit(args));
        cmdList.put("print",            (args) -> cmdPrintTree(args));
        cmdList.put("step",             (args) -> cmdTriggerStep(args));
        cmdList.put("compute",          (args) -> cmdComputation(args));
        cmdList.put("group",            (args) -> cmdGroup(args));

        // Map cmd name to description
        cmdDescription.add(new AbstractMap.SimpleEntry<String,String>("?",          "show cmd list"));
        cmdDescription.add(new AbstractMap.SimpleEntry<String,String>("net",        "set network by: net [nodeNum]"));
        cmdDescription.add(new AbstractMap.SimpleEntry<String,String>("input",      "set input by: input [fileName](path) [0/1](direction)"));
        cmdDescription.add(new AbstractMap.SimpleEntry<String,String>("init",       "start simulator"));
        cmdDescription.add(new AbstractMap.SimpleEntry<String,String>("print",      "print scribe tree"));
        cmdDescription.add(new AbstractMap.SimpleEntry<String,String>("step",       "trigger supperstep step by step"));
        cmdDescription.add(new AbstractMap.SimpleEntry<String,String>("compute",    "launch computation"));
        cmdDescription.add(new AbstractMap.SimpleEntry<String,String>("group",      "print group num"));
    }

    public Map<String, Function<List<String>, Boolean>> getCmdList() {
        return cmdList;
    }

    // Show the cmd list description
    public Boolean cmdHelp(List<String> args) {
        System.out.println("\n=================================================================");
        System.out.println("Name\t\t\tDescription");
        System.out.println("-----------------------------------------------------------------");
        for (Map.Entry<String, String> entry : cmdDescription) {
            System.out.println(entry.getKey() + "\t\t\t" + entry.getValue());
        }
        System.out.println("=================================================================\n");
        return true;
    }

    /* ************************************ Developer-defined testing function ************************************ */
    // Config network
    static final int GSE_CMD_NETWORK_ARG_NUM = 2;
    static final int GSE_CMD_NETWORK_ARG_NODE_NUM = 1;
    public Boolean cmdSetSimNetwork(List<String> args) {
        if (args.size() < GSE_CMD_NETWORK_ARG_NUM) {
            System.out.println("Error args.\n");
            return false;
        }

        clientSimulator.simSetNetwork(Integer.parseInt(args.get(GSE_CMD_NETWORK_ARG_NODE_NUM)));
        return true;
    }

    // Config inputfile
    static final int GSE_CMD_INPUT_ARG_NUM = 3;
    static final int GSE_CMD_INPUT_ARG_FILE_PATH = 1;
    static final int GSE_CMD_INPUT_ARG_DIRECTION = 2;
    public Boolean cmdSetInputFile(List<String> args) {
        if (args.size() < GSE_CMD_INPUT_ARG_NUM) {
            System.out.println("Error args.\n");
            return false;
        }

        String filePath = "./input/" + args.get(GSE_CMD_INPUT_ARG_FILE_PATH) + ".txt";
        boolean direction = Integer.parseInt(args.get(GSE_CMD_INPUT_ARG_DIRECTION)) == 1 ? true : false;
        clientSimulator.simSetInput(filePath, direction);
        return true;
    }

    // Start simulator
    public Boolean cmdInit(List<String> args) {
        clientSimulator.start();
        return true;
    }

    // Print the scribe tree
    public Boolean cmdPrintTree(List<String> args) {
        clientSimulator.simPrintTree();
        return true;
    }

    // Launch the computation step by step, that is, publish once
    public Boolean cmdTriggerStep(List<String> args) {
        clientSimulator.simTriggerSuperstep();
        return true;
    }

    // Launch the computation by publishing iteratively
    public Boolean cmdComputation(List<String> args) {
        clientSimulator.simLaunchComputation();
        return true;
    }

    // Print group result
    public Boolean cmdGroup(List<String> args) {
        clientSimulator.simGroup();
        return true;
    }
}
