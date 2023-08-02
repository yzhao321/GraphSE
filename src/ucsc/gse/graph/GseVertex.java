/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.graph;

import java.io.Serializable;
import java.util.ArrayList;

public class GseVertex implements Serializable {
    int id;
    int property;
    ArrayList<GseEdge> adjList = new ArrayList<>();

    public GseVertex(int id) {
        this(id, id);
    }

    public GseVertex(int id, int property) {
        this.id = id;
        this.property = property;
    }

    public void addEdge(GseEdge edge) {
        adjList.add(edge);
    }

    public String toString() {
        return "V[" + id + "]: " +  property;
    }
}
