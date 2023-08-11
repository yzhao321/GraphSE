/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.operator;

import rice.p2p.scribe.Topic;
import ucsc.gse.graph.GseGraph;
import ucsc.gse.graph.GseVertex;

public class GseOperatorCc implements GseOperator {
    @Override
    public void init(GseVertex target, Topic topic) {
        target.setTopicVal(topic, target.getId());
    }

    @Override
    public boolean compute(GseVertex target, GseVertex ref, Topic topic) {
        if ((int) target.getTopicVal(topic) >= (int) ref.getTopicVal(topic)) {
            return false;
        }

        // System.out.println("Update: " + target.getTopicVal(topic) + " --> " + ref.getTopicVal(topic));
        target.setTopicVal(topic, ref.getTopicVal(topic));
        return true;
    }

    @Override
    public boolean aggregate() {
        return true;
    }

    @Override
    public void fix(GseGraph localGraph, Topic topic) {
        return;
    }

    @Override
    public int evaluate(GseVertex target, Topic topic) {
        int val = (int) target.getTopicVal(topic);
        return val;
    }
}
