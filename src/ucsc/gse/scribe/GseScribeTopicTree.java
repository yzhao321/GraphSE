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
import ucsc.gse.operator.*;

public class GseScribeTopicTree {
    // Computation
    Topic treeTopic;
    GseOperator treeOperator;
    int treeSteps;

    // Tree management
    Map<NodeHandle, GseScribeNode> treeNodeMap = new HashMap<>(); // Nodes in this topic
    NodeHandle treeRoot = null; // Tree root of this topic

    // Scribe tree waiting heuristic const value
    public static final int GSE_TREE_BUILD_WAIT_TIME = 1000;
    public static final int GSE_TREE_STEP_WAIT_TIME = 500;

    public GseScribeTopicTree(Topic topic, GseOperator operator, int steps) {
        treeTopic = topic;
        treeOperator = operator;
        treeSteps = steps;
    }

    /* **************************** Topic tree interface ********************************* */
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
            scribeNode.appLocalTopicOperator.put(treeTopic, treeOperator);
        }
    }

    public void initGraphTopicVal() {
        for (GseScribeNode scribeNode : treeNodeMap.values()) {
            if (scribeNode.appLocalGraph == null) {
                continue;
            }
            for (GseVertex vertex : scribeNode.appLocalGraph.getVertexList()) {
                treeOperator.init(vertex, treeTopic);
            }
        }
    }

    public void printTree() {
        System.out.println("\n--------------Topic Tree-----------------");
        System.out.println("\t Topic: " + treeTopic);
        System.out.println("------------------------------------------");
        printChildren(treeRoot, 0);
        System.out.println("------------------------------------------\n");
    }

    public void publishUpdate() {
        treeNodeMap.get(treeRoot).publish(treeTopic, new GseScribeContentLocal(treeRoot, treeTopic, treeOperator));
    }

    public void startComputation() {
        for (int i = 0; i < treeSteps; i++) {
            System.out.println("------------------------------------------");
            System.out.println("\t\t Step " + i);
            System.out.println("------------------------------------------");
            publishUpdate();
            try {
                new SimpleTimeSource().sleep(GSE_TREE_STEP_WAIT_TIME);
            } catch (InterruptedException e) {
                System.out.println("Gse sim wait error: " + e);
            }
            System.out.println("------------------------------------------\n");
        }
        System.out.println("\t      Iteration End");
        System.out.println("------------------------------------------\n");
    }

    public void printGroupNum() {
        Set<Integer> groupIdSet = new HashSet<>();
        for (GseScribeNode node : treeNodeMap.values()) {
            if (node.appLocalGraph == null) {
                continue;
            }
            for (GseVertex vertex : node.appLocalGraph.getVertexList()) {
                groupIdSet.add(vertex.getTopicVal(treeTopic));
            }
        }
        System.out.println("------------------------------------------");
        System.out.println("\t    Group number " + groupIdSet.size());
        System.out.println("------------------------------------------\n");
    }

    public void printGroupMax() {
        int maxNum = 0;
        GseVertex maxVertex = null;
        for (GseScribeNode node : treeNodeMap.values()) {
            if (node.appLocalGraph == null) {
                continue;
            }
            for (GseVertex vertex : node.appLocalGraph.getVertexList()) {
                int val = vertex.getTopicVal(treeTopic);
                if (maxNum < val) {
                    maxNum = val;
                    maxVertex = vertex;
                }
            }
        }
        System.out.println("------------------------------------------");
        System.out.println(" " + maxVertex + " : " + maxNum);
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
