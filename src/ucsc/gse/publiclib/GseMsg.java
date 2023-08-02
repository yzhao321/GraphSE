/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.publiclib;

import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;

public class GseMsg implements Message {
    public static final String GSE_MSG_RECEIV = "RECEIVE";
    public static final String GSE_MSG_HALT = "HALT";

    String msgType;
    NodeHandle msgSrc;

    public GseMsg(String msgType, NodeHandle msgSrc) {
        this.msgType = msgType;
        this.msgSrc = msgSrc;
    }

    public String getMsgType() {
        return msgType;
    }

    public NodeHandle getSrc() {
        return msgSrc;
    }

    public int getPriority() {
        return Message.LOW_PRIORITY;
    }
}
