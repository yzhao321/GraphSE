/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ucsc.gse.operator.*;

public class GseSimComputation {
    // Computation map from name to operator
    Map<String, GseOperator> simCompMapOps = new ConcurrentHashMap<>();
    Map<String, Integer> simCompMapStep = new ConcurrentHashMap<>();
    // Computation list for simulator
    List<String> simCompStrList = new ArrayList<>();

    /* ****************************** Default value ****************************** */
    // Computation topic
    public static final String GSE_COMP_TOPIC_CC = "CC";
    public static final String GSE_COMP_TOPIC_PR = "PR";
    public static final String GSE_COMP_TOPIC_SP = "SP";
    // Computation steps
    public static final int GSE_COMP_CC_STEPS = 10;
    public static final int GSE_COMP_PR_STEPS = 10;
    public static final int GSE_COMP_SP_STEPS = 10;

    public GseSimComputation() {
        // Computation operator type
        simCompMapOps.put(GSE_COMP_TOPIC_CC, new GseOperatorCc());
        simCompMapOps.put(GSE_COMP_TOPIC_PR, new GseOperatorPr());
        simCompMapOps.put(GSE_COMP_TOPIC_SP, new GseOperatorSp());

        // Computation superstep
        simCompMapStep.put(GSE_COMP_TOPIC_CC, GSE_COMP_CC_STEPS);
        simCompMapStep.put(GSE_COMP_TOPIC_PR, GSE_COMP_PR_STEPS);
        simCompMapStep.put(GSE_COMP_TOPIC_SP, GSE_COMP_SP_STEPS);
    }

    /* ****************************** Interface for setting ********************** */
    public void simCompAddTopicOperator(String str) {
        simCompStrList.add(str);
    }
}
