/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.operator;

import java.util.HashMap;
import java.util.Map;

import rice.p2p.scribe.Topic;
import ucsc.gse.graph.GseGraph;
import ucsc.gse.graph.GseVertex;
import ucsc.gse.simulator.GseSimInput;

public class GseOperatorPr implements GseOperator {
    private static double GSE_OPERATOR_PR_RANDOM_PARA = 0.15;
    private static int GSE_OPERATOR_PR_INIT_VAL = 100000;

    @Override
    public void init(GseVertex target, Topic topic) {
        Map<Integer, Integer> targetMap = new HashMap<>();
        targetMap.put(target.getId(), GSE_OPERATOR_PR_INIT_VAL);
        target.setTopicVal(topic, targetMap);
    }

    @Override
    public boolean compute(GseVertex target, GseVertex ref, Topic topic) {
        Map<Integer, Integer> targetMap = convertObjectToMap(target.getTopicVal(topic));
        Map<Integer, Integer> refMap = convertObjectToMap(ref.getTopicVal(topic));
        targetMap.put(ref.getId(), refMap.get(ref.getId()) / ref.getOutDegree());

        // Assert whether the vertex has gather all value from src
        if (targetMap.size() < target.getInDegree() + 1) {
            return false;
        }

        // PageRank random walk
        int targetValue = targetMap.remove(target.getId());
        int sum = 0;
        for (int refVal : targetMap.values()) {
            sum += refVal;
        }
        targetValue = (int) (GSE_OPERATOR_PR_RANDOM_PARA * GSE_OPERATOR_PR_INIT_VAL / GseSimInput.simGetVertexNum() 
            + (1 - GSE_OPERATOR_PR_RANDOM_PARA) * sum);

        // Update value
        targetMap = new HashMap<>();
        targetMap.put(target.getId(), targetValue);
        target.setTopicVal(topic, targetMap);
        return true;
    }

    @Override
    public boolean aggregate() {
        return false;
    }

    @Override
    public void fix(GseGraph localGraph, Topic topic) {
        // For the vertex without src
        for (GseVertex target : localGraph.getVertexList()) {
            if (target.getInDegree() == 0) {
                int targetValue = (int) (GSE_OPERATOR_PR_RANDOM_PARA * GSE_OPERATOR_PR_INIT_VAL / GseSimInput.simGetVertexNum());
                Map<Integer, Integer> targetMap = new HashMap<>();
                targetMap.put(target.getId(), targetValue);
                target.setTopicVal(topic, targetMap);
            }
        }
    }

    @Override
    public int evaluate(GseVertex target, Topic topic) {
        Map<Integer, Integer> targetMap = convertObjectToMap(target.getTopicVal(topic));
        int val = (int) targetMap.get(target.getId());
        return val;
    }

    private Map<Integer, Integer> convertObjectToMap(Object targetObject) {
        return (Map<Integer, Integer>) targetObject;
    }
}
