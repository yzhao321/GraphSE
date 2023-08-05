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

public class GseOperatorPr implements GseOperator {
    private static double GSE_OPERATOR_PR_RANDOM_PARA = 0.15;
    private static int GSE_OPERATOR_PR_INIT_VAL = 100000;

    @Override
    public void init(GseVertex target, Topic topic) {
        target.setTopicVal(topic, GSE_OPERATOR_PR_INIT_VAL);
    }

    @Override
    public boolean compute(GseVertex target, GseVertex ref, Topic topic) {
        int targetValue = (int) ( (double)target.getTopicVal(topic) * GSE_OPERATOR_PR_RANDOM_PARA +
            (1.0 / ref.getOutDegree()) * (double)ref.getTopicVal(topic) * (1 - GSE_OPERATOR_PR_RANDOM_PARA) );
        target.setTopicVal(topic, targetValue);
        return true;
    }

    @Override
    public boolean aggregate() {
        return false;
    }

    @Override
    public void fix(GseGraph localGraph, Topic topic) {
        for (GseVertex vertex : localGraph.getVertexList()) {
            if (vertex.getOutDegree() == 0 || vertex.getInDegree() == 0) {
                vertex.setTopicVal(topic, (int) (vertex.getTopicVal(topic) * GSE_OPERATOR_PR_RANDOM_PARA));
            }
        }
    }
}
