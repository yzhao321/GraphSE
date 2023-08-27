/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import ucsc.gse.graph.*;
import ucsc.gse.scribe.GseScribeNode;

public class GseSimInput {
    // Gse input config
    String inputFilePath = GSE_SIM_INPUT_FILE_PATH;
    boolean inputGraphDirection = false;

    // Gse input file
    List<GseEdge> inputGraphEdgeList = new ArrayList<>();
    GseGraph inputGraph = new GseGraph();

    // Gse distributed storage
    static Set<Integer> inputVertexId = new HashSet<>();
    List<GseGraph> inputSubGraphList = new ArrayList<>();

    // Gse graph edge const value
    public static final int GSE_GRAPH_EDGE_SRC = 0;
    public static final int GSE_GRAPH_EDGE_DST = 1;

    // Gse default test input file
    public static final String GSE_SIM_INPUT_FILE_PATH = "./input/p2p-Gnutella08.txt";

    /* ****************************** Interface for running input reader ****************************** */
    // Initialization
    public void simInputReadFile() {
        simInputReadGraph();
        simInputProduceGraph();
    }

    // Divide input and store into sub graph set
    public void simInputDivideInput(int externNum) {
        List<Integer> graphVertexList = inputGraph.getVertexIdList();
        int capacityNum = inputGraph.getVertexNum();
        int singleNum = capacityNum / externNum;

        for (int i = 0; i < externNum; i++) {
            GseGraph subGraph;
            if (i == externNum - 1) {
                subGraph = inputGraph.divide(graphVertexList.subList(i * singleNum, capacityNum));
            } else {
                subGraph = inputGraph.divide(graphVertexList.subList(i * singleNum, (i + 1) * singleNum));
            }
            inputSubGraphList.add(subGraph);
        }
    }

    // Send sub graph from the set to other node
    public void simInputSendInput(List<GseScribeNode> workerList) {
        for (int i = 0; i < workerList.size(); i++) {
            workerList.get(i).storeGraph(inputSubGraphList.get(i));
        }
    }

    /* ****************************** Interface for setting or printing input reader ****************************** */
    public void simInputSetInput(String filePath, boolean direction) {
        inputFilePath = filePath;
        inputGraphDirection = direction;
    }

    public void simInputPrintFile() {
        System.out.println("\n---show input file---");
        for (int i = 0; i < inputGraphEdgeList.size(); i++) {
            System.out.println(inputGraphEdgeList.get(i));
        }
        System.out.println("---------------------\n");
    }

    public void simInputPrintGraph() {
        System.out.println("\n-----print graph-----");
        inputGraph.print("");
        System.out.println("---------------------\n");
    }

    /* ****************************** For operator ****************************** */
    public static int simGetVertexNum() {
        return inputVertexId.size();
    }

    /* ****************************** Read file procedure ****************************** */
    private void simInputReadGraph() {
        String line = null;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            while ((line = reader.readLine()) != null) {
                String[] edgeLine = line.split("\\s+");
                GseEdge edge = new GseEdge(Integer.parseInt(edgeLine[GSE_GRAPH_EDGE_SRC]), Integer.parseInt(edgeLine[GSE_GRAPH_EDGE_DST]));
                inputGraphEdgeList.add(edge);
            }
            reader.close();
        } catch (Exception e) {
            System.out.print("Gse sim input read file error.");
            System.out.println("e");
        }
    }

    private void simInputProduceGraph() {
        inputGraph.setDirection(inputGraphDirection);
        for (GseEdge edge : inputGraphEdgeList) {
            inputGraph.insertEdge(edge);
            if (!inputVertexId.contains(edge.getSrc())) {
                inputVertexId.add(edge.getSrc());
            }
            if (!inputVertexId.contains(edge.getDst())) {
                inputVertexId.add(edge.getDst());
            }
        }
    }
}
