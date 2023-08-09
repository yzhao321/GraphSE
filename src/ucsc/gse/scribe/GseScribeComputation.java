/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */


package ucsc.gse.scribe;

import rice.p2p.scribe.Topic;
import ucsc.gse.operator.GseOperator;

public class GseScribeComputation {
    Topic compTopic = null;
    GseOperator compOperator = null;
    int compSteps = 0;
    
    public GseScribeComputation(Topic topic, GseOperator operator, int steps) {
        compTopic = topic;
        compOperator = operator;
        compSteps = steps;
    }
}
