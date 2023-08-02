/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.client;

public class GseClientEntrance {
    public static void main(String[] args) throws Exception {
        System.out.println("\n=================================================================");
        System.out.println("-----------------------------GraphSE-----------------------------");
        System.out.println("=================================================================\n");

        // Launch a shell client for developer
        GseClientShell clientShell = new GseClientShell();
        clientShell.start();
    }
}
