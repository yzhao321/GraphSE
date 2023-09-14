/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.content;

import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.Topic;
import ucsc.gse.publiclib.GseSignal;

public class GseScribeContentManagementInit implements GseScribeContent {
    NodeHandle srcHandle;
    Topic topic;

    public GseScribeContentManagementInit(NodeHandle srcHandle, Topic topic) {
        this.srcHandle = srcHandle;
        this.topic = topic;
    }

    @Override
    public int run(Object contentObject) {
        return GseSignal.GSE_SIGNAL_REQ_ADDR;
    }

    @Override
    public NodeHandle getSrc() {
        return srcHandle;
    }

    @Override
    public Topic getTopic() {
        return topic;
    }
}
