/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.operator;

import rice.p2p.scribe.Topic;
import ucsc.gse.graph.GseVertex;

public class GseOperatorCc implements GseOperator {
    @Override
    public void init(GseVertex target, Topic topic) {
        target.setTopicVal(topic, target.getId());
    }

    @Override
    public boolean compute(GseVertex target, GseVertex ref, Topic topic) {
        boolean changedFlag = false;

        if (target.getTopicVal(topic) < ref.getTopicVal(topic)) {
            System.out.println("Update: " + target.getTopicVal(topic) + " --> " + ref.getTopicVal(topic));
            target.setTopicVal(topic, ref.getTopicVal(topic));
            changedFlag = true;
        }
        return changedFlag;
    }

    @Override
    public boolean aggregate() {
        return true;
    }
}
