/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.operator;

import java.io.Serializable;

import rice.p2p.scribe.Topic;
import ucsc.gse.graph.GseGraph;
import ucsc.gse.graph.GseVertex;

public interface GseOperator extends Serializable {
    // Initialize the value of vertex for this operator
    public void init(GseVertex target, Topic topic);

    // Update the target value comparing the ref
    public boolean compute(GseVertex target, GseVertex ref, Topic topic);

    // Return true when subgraph can be aggregated loccally
    public boolean aggregate();

    // Adjust in some scenarios
    public void fix(GseGraph localGraph, Topic topic);

    // Evaluation for showing results
    public int evaluate(GseVertex target, Topic topic);
}
