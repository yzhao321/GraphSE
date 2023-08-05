/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import rice.p2p.scribe.Topic;

public class GseVertex implements Serializable {
    int id;
    int property;

    int inDegree = 0;
    int outDegree = 0;

    ArrayList<GseEdge> adjList = new ArrayList<>();
    Map<Topic, Integer> topicMap = new HashMap<>();

    public GseVertex(int id) {
        this(id, 0);
    }

    public GseVertex(int id, int property) {
        this.id = id;
        this.property = property;
    }

    public void addEdge(GseEdge edge) {
        adjList.add(edge);
    }

    public int getId() {
        return id;
    }

    public int getInDegree() {
        return inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public void setTopicVal(Topic topic, int val) {
        topicMap.put(topic, val);
    }

    public int getTopicVal(Topic topic) {
        return topicMap.get(topic);
    }

    public String toString() {
        String val = "";
        for (Map.Entry<Topic, Integer> topicVal : topicMap.entrySet()) {
            val += topicVal.toString();
        }
        return "V[" + id + ": " + val + "]";
    }
}
