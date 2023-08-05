/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.simulator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

import rice.environment.Environment;
import rice.environment.time.simple.SimpleTimeSource;

import rice.p2p.scribe.Topic;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

import ucsc.gse.scribe.*;

public class GseSim extends Thread {
    // Gse pastry environment
    Environment env;
    NodeIdFactory nidFactory;
    PastryNodeFactory factory;
    InetSocketAddress bootSocketAddr;

    // Gse scribe management
    ArrayList<GseScribeNode> simScribeNodes = new ArrayList<GseScribeNode>();
    GseScribeTopicTree simScribeTree = null;

    // Gse config
    GseSimNetwork simNet = new GseSimNetwork();
    GseSimInput simInput = new GseSimInput();
    GseSimComputation simComputation = new GseSimComputation();

    // Gse waiting heuristic const value
    public static final int GSE_SIM_NETWORK_SET_WAIT_TIME = 100;
    public static final int GSE_SIM_NODE_JOIN_WAIT_TIME = 100;

    @Override
    public void run() {
        // Construct scribe topic tree
        simBuildEnvironment();
        simCreateNode();
        simBuildTree();

        // Divide data into workers
        simReadInput();
        simDivideInput();
        simInitComputation();
    }

    /* ****************************** Interface for shell cmd ****************************** */
    public void simSetNetwork(int nodeNum) {
        simNet.simNetSetNodeNum(nodeNum);
    }

    public void simSetInput(String filePath, boolean direction) {
        simInput.simInputSetInput(filePath, direction);;
    }

    public void simSetComputation(String str, int steps) {
        simComputation.simCompSetTopicOperator(str);
        simComputation.simCompSetSteps(steps);
    }

    public void simPrintTree() {
        simScribeTree.printTree();
    }

    public void simTriggerSuperstep() {
        simScribeTree.publishUpdate();
    }

    public void simLaunchComputation() {
        simScribeTree.startComputation();
    }

    public void simResultGroup() {
        simScribeTree.printGroupNum();
    }

    public void simResultMax() {
        simScribeTree.printGroupMax();
    }

    /* ****************************** Start Procedure ****************************** */
    private void simBuildEnvironment() {
        // Init pastry environment
        try {
            env = new Environment();
            nidFactory = new RandomNodeIdFactory(env);
            InetAddress bootaddr = InetAddress.getByName(simNet.simNetGetIpAddress());
            bootSocketAddr = new InetSocketAddress(bootaddr, simNet.simNetGetBootPort());
            factory = new SocketPastryNodeFactory(nidFactory, simNet.simNetGetBindPort(), env);

            // Wait for network
            new SimpleTimeSource().sleep(GSE_SIM_NETWORK_SET_WAIT_TIME);
        } catch (Exception e) {
            System.out.println("Gse simulater create error: " + e);
        }
    }

    private void simCreateNode() {
        try {
            System.out.println("\n--------------Node Creating-----------------");
            for (int i = 0; i < simNet.simNetGetNodeNum(); i++) {
                PastryNode pastryNode = factory.newNode();
                GseScribeNode scribeNode = new GseScribeNode(pastryNode);
                simScribeNodes.add(scribeNode);
                pastryNode.boot(bootSocketAddr);

                // Wait for node ready
                synchronized(pastryNode) {
                    while (!pastryNode.isReady() && !pastryNode.joinFailed()) {
                        pastryNode.wait(GSE_SIM_NODE_JOIN_WAIT_TIME);

                        if (pastryNode.joinFailed()) {
                            throw new IOException("GraphSE create pastry nodes fail: " + pastryNode.joinFailedReason());
                        }
                    }
                }
                System.out.println("GraphSE create pastry node succ: " + pastryNode);
            }
            System.out.println("------------------------------------------");
        } catch (Exception e) {
            System.out.println("Gse simulater create node error: " + e);
        }
    }

    private void simBuildTree() {
        simScribeTree = new GseScribeTopicTree(
            new Topic(new PastryIdFactory(env), simComputation.simCompTopicString),
            simComputation.simCompOperator,
            simComputation.simCompSteps
        );
        simScribeTree.buildTree(simScribeNodes);

        GseScribeNode master = null;
        for (GseScribeNode scribeNode : simScribeNodes) {
            if (scribeNode.isRootOfTopics()) {
                master = scribeNode;
                break;
            }
        }
        simScribeNodes.remove(master);
    }

    private void simReadInput() {
        simInput.simInputReadFile();
    }

    private void simDivideInput() {
        simInput.simInputDivideInput(simScribeNodes);
    }

    private void simInitComputation() {
        simScribeTree.initGraphTopicVal();
    }
}
