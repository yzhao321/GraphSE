/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.msg;

import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;

public class GseMsg implements Message {
    public static final String GSE_MSG_TYPE_RECEIV = "RECEIVE";
    public static final String GSE_MSG_TYPE_HALT = "HALT";

    NodeHandle msgSrc;
    String msgType;

    public GseMsg(NodeHandle msgSrc, String msgType) {
        this.msgSrc = msgSrc;
        this.msgType = msgType;
    }

    public NodeHandle getSrc() {
        return msgSrc;
    }

    public String getType() {
        return msgType;
    }

    public int getPriority() {
        return Message.LOW_PRIORITY;
    }
}
