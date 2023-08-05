/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.operator;

import java.io.Serializable;

import rice.p2p.scribe.Topic;
import ucsc.gse.graph.GseVertex;

public interface GseOperator extends Serializable {
    public void init(GseVertex target, Topic topic);
    public boolean compute(GseVertex target, GseVertex ref, Topic topic);
}
