/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.scribe;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import rice.environment.time.simple.SimpleTimeSource;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.Topic;

import ucsc.gse.content.*;
import ucsc.gse.graph.GseVertex;

public class GseScribeTopicTree extends Thread {
    // Computation
    Topic treeTopic;
    GseScribeComputation treeComputation;

    // Tree management
    Map<NodeHandle, GseScribeNode> treeNodeMap = new HashMap<>(); // Nodes in this topic
    NodeHandle treeRoot = null; // Tree root of this topic

    // Scribe tree waiting heuristic const value
    public static final int GSE_TREE_BUILD_WAIT_TIME = 1000;
    public static final int GSE_TREE_STEP_WAIT_TIME = 2000;

    public GseScribeTopicTree(Topic topic, GseScribeComputation computation) {
        treeTopic = topic;
        treeComputation = computation;
    }

    /* **************************** Topic tree computation ********************************* */
    public void publishUpdate() {
        treeNodeMap.get(treeRoot).publish(treeTopic, new GseScribeContentLocal(treeRoot, treeTopic, treeComputation.compOperator));
    }

    public void startComputation() {
        System.out.println("\n");
        for (int i = 0; i < treeComputation.compSteps; i++) {
            System.out.println("------------------------------------------");
            System.out.println("\t " + treeTopic + " step " + i);
            publishUpdate();
            try {
                new SimpleTimeSource().sleep(GSE_TREE_STEP_WAIT_TIME);
            } catch (InterruptedException e) {
                System.out.println("Gse sim wait error: " + e);
            }
        }
        System.out.println("------------------------------------------");
        System.out.println("\t " + treeTopic + " iteration End\n");
    }

    @Override
    public void run() {
        startComputation();
    }

    /* **************************** Topic tree initialization ********************************* */
    public void buildTree(ArrayList<GseScribeNode> scribeNodes) {
        // Subscribe same topic for all nodes
        for (GseScribeNode scribeNode : scribeNodes) {
            scribeNode.subscribe(treeTopic);
        }

        // Wait until tree connect
        try {
            new SimpleTimeSource().sleep(GSE_TREE_BUILD_WAIT_TIME);
        } catch (Exception e) {
            System.out.println("Gse topic tree building wait error: " + e);
        }

        // Record application table and root
        for (GseScribeNode scribeNode : scribeNodes) {
            treeNodeMap.put(scribeNode.appLocalEndpoint.getLocalNodeHandle(), scribeNode);
        }
        treeRoot = getRoot(scribeNodes.get(0).appLocalEndpoint.getLocalNodeHandle());

        // Add topic info
        for (GseScribeNode scribeNode : scribeNodes) {
            scribeNode.appLocalTopics.add(treeTopic);
            scribeNode.appLocalTopicOperator.put(treeTopic, treeComputation.compOperator);
        }
    }

    public void initGraphTopicVal() {
        for (GseScribeNode scribeNode : treeNodeMap.values()) {
            if (scribeNode.appLocalGraph == null) {
                continue;
            }
            for (GseVertex vertex : scribeNode.appLocalGraph.getVertexList()) {
                treeComputation.compOperator.init(vertex, treeTopic);
            }
        }
    }


    /* **************************** Topic tree result viewing ********************************* */
    public void printTree() {
        System.out.println("\n--------------Topic Tree-----------------");
        System.out.println("\t Topic: " + treeTopic);
        System.out.println("------------------------------------------");
        printChildren(treeRoot, 0);
        System.out.println("------------------------------------------\n");
    }

    public void printGroupNum() {
        Set<Integer> groupIdSet = new HashSet<>();
        for (GseScribeNode node : treeNodeMap.values()) {
            if (node.appLocalGraph == null) {
                continue;
            }
            for (GseVertex vertex : node.appLocalGraph.getVertexList()) {
                groupIdSet.add(treeComputation.compOperator.evaluate(vertex, treeTopic));
            }
        }
        System.out.println("------------------------------------------");
        System.out.println("Result of group number: " + groupIdSet.size());
        System.out.println("------------------------------------------\n");
    }

    public void printMax() {
        int maxNum = 0;
        GseVertex maxVertex = null;
        for (GseScribeNode node : treeNodeMap.values()) {
            if (node.appLocalGraph == null) {
                continue;
            }
            for (GseVertex vertex : node.appLocalGraph.getVertexList()) {
                int val = treeComputation.compOperator.evaluate(vertex, treeTopic);
                if (maxNum < val) {
                    maxNum = val;
                    maxVertex = vertex;
                }
            }
        }
        System.out.println("------------------------------------------");
        System.out.println("Result of max num: " + maxVertex + " --> " + maxNum);
        System.out.println("------------------------------------------\n");
    }

    /* **************************** Recursive tree searching ********************************* */
    private NodeHandle getRoot(NodeHandle curHandle) {
        GseScribeNode node = treeNodeMap.get(curHandle);
        if (node.isRoot(treeTopic)) {
            return curHandle;
        }
        return getRoot(node.getParent(treeTopic));
    }

    private void printChildren(NodeHandle curHandle, int depth) {
        // Print tab for each level before current node
        String head = "";
        for (int i = 0; i < depth; i++) {
            head += "   ";
        }
        System.out.println(head + curHandle.getId().toString());
        treeNodeMap.get(curHandle).printGraph(head);

        // Print children
        Collection<NodeHandle> children = treeNodeMap.get(curHandle).getChildren(treeTopic);
        for (NodeHandle child : children) {
            printChildren(child, depth + 1);
        }
    }
}
