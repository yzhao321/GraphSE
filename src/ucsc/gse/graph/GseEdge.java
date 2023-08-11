/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.graph;

import java.io.Serializable;

public class GseEdge implements Serializable {
    int src;
    int dst;
    int weight;

    public GseEdge(int src, int dst) {
        this(src, dst, 1);
    }

    public GseEdge(int src, int dst, int weight) {
        this.src = src;
        this.dst = dst;
        this.weight = weight;
    }

    public int getSrc() {
        return src;
    }

    public int getDst() {
        return dst;
    }

    public int getWeight() {
        return weight;
    }

    public String toString() {
        return "E[" + src + ", " + dst + "]: " + weight;
    }
}
