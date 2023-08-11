/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.graph;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

import rice.p2p.scribe.Topic;

public class GseVertex implements Serializable {
    int id;
    Object property;

    int inDegree = 0;
    int outDegree = 0;

    Map<Integer, GseEdge> adjList = new HashMap<>();
    Map<Topic, Object> topicMap = new HashMap<>();

    public GseVertex(int id) {
        this(id, null);
    }

    public GseVertex(int id, Object property) {
        this.id = id;
        this.property = property;
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

    public void addEdge(GseEdge edge) {
        adjList.put(edge.dst, edge);
    }

    public GseEdge getEdge(int id) {
        return adjList.get(id);
    }

    public void setTopicVal(Topic topic, Object val) {
        topicMap.put(topic, val);
    }

    public Object getTopicVal(Topic topic) {
        return topicMap.get(topic);
    }

    public String toString() {
        String val = "";
        for (Map.Entry<Topic, Object> topicVal : topicMap.entrySet()) {
            val += topicVal.toString() + " ";
        }
        return "V{" + id + " : " + val + "}";
    }
}
