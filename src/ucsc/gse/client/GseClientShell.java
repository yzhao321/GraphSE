/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import ucsc.gse.simulator.GseSim;

public class GseClientShell {
    Map<String, Function<List<String>, Boolean>> cmdList;
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    String inputLine = null;
    public static final int GSE_CLIENT_SHELL_CMD_NAME = 0;

    public GseClientShell(GseSim clientSimulator) {
        GseClientShellCmd clientShellCmd = new GseClientShellCmd(clientSimulator);
        cmdList = clientShellCmd.getCmdList();
    }

    public void start() {
        while (true) {
            readCmd();
            implCmd();
        }
    }

    private void readCmd() {
        try {
            do {
                System.out.print("GraphSE:/>>");
                inputLine = bufferedReader.readLine();
            } while (inputLine.matches("\\s*"));
        } catch (Exception e) {
            System.out.print("Gse client cmd read error: " + e);
        }
    }

    private void implCmd() {
        List<String> inputCmd = Arrays.asList(inputLine.split("\\s+"));
        if (inputCmd.size() <= 0) {
            return;
        }

        // Match cmd
        String cmdName = inputCmd.get(GSE_CLIENT_SHELL_CMD_NAME);
        if (!cmdList.containsKey(cmdName)) {
            System.out.println("Error!!! [" + cmdName + "] not found, input ? to get the cmd list.\n");
            return;
        }

        // Run cmd
        cmdList.get(cmdName).apply(inputCmd);
    }
}
