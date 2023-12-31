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
import java.util.concurrent.ConcurrentHashMap;

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

public class GseSim {
    // Gse pastry environment
    Environment env;
    NodeIdFactory nidFactory;
    PastryNodeFactory factory;
    InetSocketAddress bootSocketAddr;

    // Gse scribe node list
    List<GseScribeNode> simLocalScribeNodes = new ArrayList<GseScribeNode>();
    // Gse scribe topic tree map
    Map<String, GseScribeTopicTree> simTopicTreeMap = new ConcurrentHashMap<>();

    // Gse config
    GseSimNetwork simNet = new GseSimNetwork();
    GseSimInput simInput = new GseSimInput();
    GseSimComputation simComputation = new GseSimComputation();
    GseSimNode simNode = new GseSimNode();

    // Gse waiting heuristic const value
    public static final int GSE_SIM_NETWORK_SET_WAIT_TIME = 100;
    public static final int GSE_SIM_NODE_JOIN_WAIT_TIME = 100;

    public void runSim() {
        // Init pastry environment
        simBuildEnvironment();
        simCreateNode();

        // Construct topic tree
        simBuildTree();

        // Divide data into workers
        simReadInput();
        simDivideInput();
        simSendInput();

        // Init vertex value for each computation
        simInitComputation();
    }

    /* ****************************** Interface for shell cmd ****************************** */
    public void simSetNodeNum(int local, int extern) {
        simNode.simNodeSetLocalNum(local);
        simNode.simNodeSetExternNum(extern);
    }

    public void simSetNetworkAddress(String address) {
        simNet.simNetSetIpAddress(address);
    }

    public void simSetInput(String filePath, boolean direction) {
        simInput.simInputSetInput(filePath, direction);
    }

    public void simAddComputation(String str) {
        simComputation.simCompAddTopicOperator(str);
    }

    public void simPrintTree(String treeStr) {
        if (!simTopicTreeMap.containsKey(treeStr)) {
            System.out.println("Error topic. ");
            return;
        }
        simTopicTreeMap.get(treeStr).printTree();
    }

    public void simTriggerSuperstep(String treeStr) {
        if (!simTopicTreeMap.containsKey(treeStr)) {
            System.out.println("Error topic. ");
            return;
        }
        simTopicTreeMap.get(treeStr).publishComputation();
    }

    public void simLaunchComputation() {
        for (GseScribeTopicTree topicTree : simTopicTreeMap.values()) {
            topicTree.start();
        }
    }

    public void simResultGroup(String treeStr) {
        if (!simTopicTreeMap.containsKey(treeStr)) {
            System.out.println("Error topic. ");
            return;
        }
        simTopicTreeMap.get(treeStr).printGroupNum();
    }

    public void simResultMax(String treeStr) {
        if (!simTopicTreeMap.containsKey(treeStr)) {
            System.out.println("Error topic. ");
            return;
        }
        simTopicTreeMap.get(treeStr).printMax();
    }

    /* ****************************** Start Procedure ****************************** */
    private void simBuildEnvironment() {
        // Init pastry environment
        try {
            env = new Environment();
            nidFactory = new RandomNodeIdFactory(env);
            InetAddress bootaddr = simNet.simNetGetIpAddress();
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
            for (int i = 0; i < simNode.simNodeGetLocalNum(); i++) {
                PastryNode pastryNode = factory.newNode();
                GseScribeNode scribeNode = new GseScribeNode(pastryNode);
                simLocalScribeNodes.add(scribeNode);
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
        // Construct a topic tree for each computation
        for (String compStr : simComputation.simCompStrList) {
            Topic compTopic = new Topic(new PastryIdFactory(env), compStr);
            GseScribeComputation simScribeComputation = new GseScribeComputation(
                compTopic,
                simComputation.simCompMapOps.get(compStr),
                simComputation.simCompMapStep.get(compStr)
            );

            GseScribeTopicTree topicTree = new GseScribeTopicTree(compTopic, simScribeComputation);
            topicTree.joinTreeTopic(simLocalScribeNodes);
            simTopicTreeMap.put(compStr, topicTree);
        }
    }

    private void simReadInput() {
        simInput.simInputReadFile();
    }

    private void simDivideInput() {
        simInput.simInputDivideInput(simNode.simNodeGetExternNum());
    }

    private void simSendInput() {
        for (GseScribeNode worker : simLocalScribeNodes) {
            simNode.simNodeAddWorkerList(worker);
        }
        simInput.simInputSendInput(simNode.simNodeGetWokerList());
    }

    private void simInitComputation() {
        for (GseScribeTopicTree topicTree : simTopicTreeMap.values()) {
            topicTree.initGraphTopicVal();
        }
    }
}
