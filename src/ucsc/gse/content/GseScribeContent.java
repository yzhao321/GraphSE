/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.content;

import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.Topic;
import rice.p2p.commonapi.NodeHandle;

public interface GseScribeContent extends ScribeContent {
    public int run(Object contentObject);
    public NodeHandle getSrc();
    public Topic getTopic();
}
