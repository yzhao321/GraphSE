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
import java.util.HashMap;
import java.util.Map;

import ucsc.gse.operator.GseOperator;

public class GseGraph implements Serializable {
    Map<Integer, GseVertex> vertexMap = new HashMap<>();
    boolean direction = false;

    /* **************************** Graph set interface ***************************** */
    /* ******** Set Weight ******** */
    // Propaget property local
    public boolean updateVertexPropertyInLocal(GseOperator operator) {
        boolean changedFlag = false;
        for (GseVertex vertex : vertexMap.values()) {
            for (GseEdge edge : vertex.adjList) {
                if (vertexMap.containsKey(edge.dst)) {
                    int rst = operator.compute(vertex.property, vertexMap.get(edge.dst).property);
                    if (vertex.property != rst) {
                        changedFlag = true;
                        vertex.property = rst;
                    }
                }
            }
        }
        return changedFlag;
    }

    // Propagete property from remote graph to current graph
    public boolean updateVertexPropertyFromRemote(GseOperator operator, GseGraph remoteGraph) {
        boolean changedFlag = false;
        for (GseVertex remoteVertex : remoteGraph.vertexMap.values()) {
            for (GseEdge remoteEdge : remoteVertex.adjList) {
                if (vertexMap.containsKey(remoteEdge.dst)) {
                    int rst = operator.compute(vertexMap.get(remoteEdge.dst).property, remoteVertex.property);
                    if (vertexMap.get(remoteEdge.dst).property != rst) {
                        changedFlag = true;
                        vertexMap.get(remoteEdge.dst).property = rst;
                    }
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
        vertexMap.get(edge.src).adjList.add(edge);

        if (!vertexMap.containsKey(edge.dst)) {
            vertexMap.put(edge.dst, new GseVertex(edge.dst));
        }

        // Graph with direction do not add edge for dst
        if (direction) {
            return;
        }
        vertexMap.get(edge.dst).adjList.add(new GseEdge(edge.dst, edge.src));
    }

    public GseGraph divide(List<Integer> vertexIdList) {
        GseGraph graph = new GseGraph();
        for (Integer id : vertexIdList) {
            graph.addVertex(this.popVertex(id));
        }
        return graph;
    }

    /* ******** New graph ******** */
    public List<Integer> aggregateRemoteList() {
        List<Integer> remoteList = new ArrayList<>();
        for (int vertexId : vertexMap.keySet()) {
            for (GseEdge edge : vertexMap.get(vertexId).adjList) {
                if (!vertexMap.containsKey(edge.dst)) {
                    remoteList.add(vertexId);
                    break;
                }
            }
        }
        return remoteList;
    }

    public GseGraph aggregate(List<Integer> vertexIdList) {
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

    public List<Integer> getVertexList() {
        return new ArrayList<>(vertexMap.keySet());
    }

    public int getProperty(int id) {
        return vertexMap.get(id).property;
    }

    public void print(String head) {
        for (GseVertex vertex : vertexMap.values()) {
            System.out.println(head + vertex);
            for (GseEdge edge : vertex.adjList) {
                System.out.println(head + "   " + edge);
            }
        }
    }
}