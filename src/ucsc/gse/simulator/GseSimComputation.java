/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.simulator;

import java.util.HashMap;
import java.util.Map;

import ucsc.gse.operator.*;

public class GseSimComputation {
    int simCompSteps = GSE_SIM_STEPS;
    String simCompTopicString = GSE_SIM_TOPIC_CC_COMP;
    GseOperator simCompOperator = null;

    Map<String, GseOperator> simCompMap = new HashMap<>();

    /* ****************************** Default value ****************************** */
    // Computation topic
    public static final String GSE_SIM_TOPIC_CC_COMP = "CC";
    public static final String GSE_SIM_TOPIC_PR_COMP = "PR";
    // Iteration steps
    public static final int GSE_SIM_STEPS = 40;

    public GseSimComputation() {
        // Computation operator type
        simCompMap.put(GSE_SIM_TOPIC_CC_COMP, new GseOperatorCc());
        simCompMap.put(GSE_SIM_TOPIC_PR_COMP, new GseOperatorPr());

        simCompOperator = simCompMap.get(GSE_SIM_TOPIC_CC_COMP);
    }

    /* ****************************** Interface for setting ********************** */
    public void simCompSetSteps(int steps) {
        simCompSteps = steps;
    }

    public void simCompSetTopicOperator(String str) {
        simCompTopicString = str;
        simCompOperator = simCompMap.get(str);
    }
}
