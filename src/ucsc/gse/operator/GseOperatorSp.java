/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.operator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rice.p2p.scribe.Topic;
import ucsc.gse.graph.GseGraph;
import ucsc.gse.graph.GseVertex;

public class GseOperatorSp implements GseOperator {
    @Override
    public void init(GseVertex target, Topic topic) {
        Map<Integer, Integer> targetMap = new ConcurrentHashMap<>();
        targetMap.put(target.getId(), 0);
        target.setTopicVal(topic, targetMap);
    }

    @Override
    public boolean compute(GseVertex target, GseVertex ref, Topic topic) {
        Map<Integer, Integer> targetMap = convertObjectToMap(target.getTopicVal(topic));
        Map<Integer, Integer> refMap = convertObjectToMap(ref.getTopicVal(topic));
        int distToRef = ref.getEdge(target.getId()).getWeight();
        boolean changeFlag = false;

        // Dijkstra
        for (Map.Entry<Integer, Integer> distToVertex : refMap.entrySet()) {
            int val = distToRef + distToVertex.getValue();
            if (!targetMap.containsKey(distToVertex.getKey())) {
                targetMap.put(distToVertex.getKey(), val);
                changeFlag = true;
                continue;
            }
            if (targetMap.get(distToVertex.getKey()) > val) {
                targetMap.replace(distToVertex.getKey(), val);
                changeFlag = true;
            }
        }

        return changeFlag;
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
        Map<Integer, Integer> targetMap = convertObjectToMap(target.getTopicVal(topic));
        int val = (int) targetMap.get(target.getId());
        return val;
    }

    private Map<Integer, Integer> convertObjectToMap(Object targetObject) {
        return (Map<Integer, Integer>) targetObject;
    }
}
