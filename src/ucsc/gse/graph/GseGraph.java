/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.graph;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rice.p2p.scribe.Topic;
import ucsc.gse.operator.GseOperator;

public class GseGraph implements Serializable {
    Map<Integer, GseVertex> vertexMap = new HashMap<>();
    boolean direction = false;

    /* **************************** Graph set interface ***************************** */
    /* ******** Set Weight ******** */
    // Propaget value local
    public boolean updateVertexValueInLocal(GseOperator operator, Topic topic) {
        boolean changedFlag = false;
        for (GseVertex vertex : vertexMap.values()) {
            for (GseEdge edge : vertex.adjList.values()) {
                if (!vertexMap.containsKey(edge.dst)) {
                    continue;
                }
                if (operator.compute(vertexMap.get(edge.dst), vertex, topic)) {
                    changedFlag = true;
                }
            }
        }
        return changedFlag;
    }

    // Propagete value from remote graph to current graph
    public boolean updateVertexValueFromRemote(GseOperator operator, GseGraph remoteGraph, Topic topic) {
        boolean changedFlag = false;
        for (GseVertex remoteVertex : remoteGraph.vertexMap.values()) {
            for (GseEdge remoteEdge : remoteVertex.adjList.values()) {
                if (!vertexMap.containsKey(remoteEdge.dst)) {
                    continue;
                }
                if (operator.compute(vertexMap.get(remoteEdge.dst), remoteVertex, topic)) {
                    changedFlag = true;
                }
            }
        }
        return changedFlag;
    }

    /* ******** Set Struct ******** */
    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public void addVertex(GseVertex vertex) {
        vertexMap.put(vertex.id, vertex);
    }

    public GseVertex popVertex(int id) {
        return vertexMap.remove(id);
    }

    public void insertEdge(GseEdge edge) {
        if (!vertexMap.containsKey(edge.src)) {
            vertexMap.put(edge.src, new GseVertex(edge.src));
        }
        vertexMap.get(edge.src).addEdge(edge);

        if (!vertexMap.containsKey(edge.dst)) {
            vertexMap.put(edge.dst, new GseVertex(edge.dst));
        }

        vertexMap.get(edge.src).outDegree++;
        vertexMap.get(edge.dst).inDegree++;

        // Graph with direction do not add edge for dst
        if (direction) {
            return;
        }
        vertexMap.get(edge.dst).addEdge(new GseEdge(edge.dst, edge.src));

        vertexMap.get(edge.src).inDegree++;
        vertexMap.get(edge.dst).outDegree++;
    }

    public GseGraph divide(List<Integer> vertexIdList) {
        GseGraph graph = new GseGraph();
        for (Integer id : vertexIdList) {
            graph.addVertex(this.popVertex(id));
        }
        return graph;
    }

    /* ******** New graph ******** */
    public List<Integer> reduceRemoteList() {
        List<Integer> remoteList = new ArrayList<>();
        for (int vertexId : vertexMap.keySet()) {
            for (GseEdge edge : vertexMap.get(vertexId).adjList.values()) {
                if (!vertexMap.containsKey(edge.dst)) {
                    remoteList.add(vertexId);
                    break;
                }
            }
        }
        return remoteList;
    }

    public GseGraph reduce(List<Integer> vertexIdList) {
        GseGraph remoteGraph = new GseGraph();
        remoteGraph.direction = this.direction;
        for (Integer vertexId : vertexIdList) {
            remoteGraph.vertexMap.put(vertexId, this.vertexMap.get(vertexId));
        }
        return remoteGraph;
    }

    /* **************************** Graph get interface ***************************** */
    public int getVertexNum() {
        return vertexMap.size();
    }

    public List<Integer> getVertexIdList() {
        return new ArrayList<>(vertexMap.keySet());
    }

    public Collection<GseVertex> getVertexList() {
        return vertexMap.values();
    }

    public Object getProperty(int id) {
        return vertexMap.get(id).property;
    }

    public void print(String head) {
        for (GseVertex vertex : vertexMap.values()) {
            System.out.println(head + vertex);
            for (GseEdge edge : vertex.adjList.values()) {
                System.out.println(head + "   " + edge);
            }
        }
    }
}
