/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.simulator;

import rice.p2p.commonapi.NodeHandle;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ucsc.gse.scribe.GseScribeNode;

public class GseSimNode {
    /* ****************************** Node management ****************************** */
    List<GseScribeNode> workerList = new ArrayList<>();
    Map<NodeHandle, List<Integer>> handleToVertext = new ConcurrentHashMap<>();

    /* ****************************** Node num ****************************** */
    // Local node num can be used in heterogeneous environments
    int simNodeLocalNum = GSE_SIM_NODE_LOCAL_NUM;
    // Extern node num is the total num in the whole edge distributed system
    int simNodeExternNum = GSE_SIM_NODE_EXTERN_NUM;

    /* ****************************** Default value ****************************** */
    public static final int GSE_SIM_NODE_LOCAL_NUM = 1;
    public static final int GSE_SIM_NODE_EXTERN_NUM = 2;

    /* ****************************** Interface for set/get ********************** */
    public void simNodeSetLocalNum(int nodeNum) {
        simNodeLocalNum = nodeNum;
    }

    public void simNodeSetExternNum(int nodeNum) {
        simNodeExternNum = nodeNum;
    }

    public int simNodeGetLocalNum() {
        return simNodeLocalNum;
    }

    public int simNodeGetExternNum() {
        return simNodeExternNum;
    }

    public List<GseScribeNode> simNodeGetWokerList() {
        return workerList;
    }

    public void simNodeAddWorkerList(GseScribeNode worker) {
        workerList.add(worker);
    }

    public Map<NodeHandle, List<Integer>> simNodeGetWorkerMap() {
        return handleToVertext;
    }

    public void simNodeAddWorkerMap(NodeHandle node, List<Integer> vertexList) {
        handleToVertext.put(node, vertexList);
    }
}
