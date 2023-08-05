/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.content;

import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.Topic;

import ucsc.gse.operator.GseOperator;
import ucsc.gse.graph.GseGraph;
import ucsc.gse.publiclib.GseSignal;

public class GseScribeContentLocal implements GseScribeContent {
    NodeHandle srcHandle;
    Topic topic;
    GseOperator operator;

    public GseScribeContentLocal(NodeHandle srcHandle, Topic topic, GseOperator operator) {
        this.srcHandle = srcHandle;
        this.topic = topic;
        this.operator = operator;
    }

    @Override
    public int run(GseGraph localGraph) {
        int contentSignal = GseSignal.GSE_SIGNAL_LOCAL_HALT;
        if (localGraph.updateVertexPropertyInLocal(operator, topic)) {
            contentSignal = GseSignal.GSE_SIGNAL_LOCAL_PUB;
        }
        return contentSignal;
    }

    @Override
    public NodeHandle getSrc() {
        return srcHandle;
    }

    @Override
    public Topic getTopic() {
        return topic;
    }

    public String toString() {
        return "Update msg from " + srcHandle;
    }
}
