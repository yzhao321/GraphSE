/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.operator;

public class GseOperatorMax implements GseOperator {
    @Override
    public int compute(int local, int remote) {
        if (local < remote) {
            System.out.println("Update Vertex: " + local + " --> " + remote);
        }
        return local < remote ? remote : local;
    }
}
