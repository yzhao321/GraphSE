/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.content;

import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.Topic;
import ucsc.gse.graph.GseGraph;
import ucsc.gse.operator.GseOperator;
import ucsc.gse.publiclib.GseSignal;

public class GseScribeContentComputationRemote implements GseScribeContent {
    NodeHandle srcHandle;
    GseGraph remoteGraph;
    Topic topic;
    GseOperator operator;

    public GseScribeContentComputationRemote(NodeHandle srcHandle, GseGraph remoteGraph, Topic topic, GseOperator operator) {
        this.srcHandle = srcHandle;
        this.remoteGraph = remoteGraph;
        this.topic = topic;
        this.operator = operator;
    }

    @Override
    public int run(Object contentObject) {
        GseGraph localGraph = (GseGraph) contentObject;
        int contentSignal = GseSignal.GSE_SIGNAL_REMOTE_HALT;
        if (localGraph.updateVertexValueFromRemote(operator, remoteGraph, topic)) {
            contentSignal = GseSignal.GSE_SIGNAL_REMOTE_RECV;
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
        return "Propagate msg from " + srcHandle;
    }
}
