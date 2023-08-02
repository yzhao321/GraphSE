/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.simulator;

import ucsc.gse.operator.*;

public class GseSimComputation {
    int simCompSteps = GSE_SIM_STEPS;
    String simCompTopicString = GSE_SIM_TOPIC_CONN_COMP;
    GseOperator simComOperator = new GseOperatorMax();

    /* ****************************** Default value ****************************** */
    // Computation topic
    public static final String GSE_SIM_TOPIC_CONN_COMP = "ConnectedComponent";
    // Iteration steps
    public static final int GSE_SIM_STEPS = 60;

    /* ****************************** Interface for setting ********************** */
    public void simCompSetSteps(int steps) {
        simCompSteps = steps;
    }

    public void simCompSetTopicString(String str) {
        simCompTopicString = str;
    }

    public void simCompSetOperator(GseOperator operator) {
        simComOperator = operator;
    }
}
