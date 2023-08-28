/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.publiclib;

public class GseSignal {
    public static final int GSE_SIGNAL_VOID = 0x00;

    public static final int GSE_SIGNAL_LOCAL_HALT = 0x11;
    public static final int GSE_SIGNAL_LOCAL_PUB = 0x12;
    public static final int GSE_SIGNAL_REMOTE_HALT = 0x13;
    public static final int GSE_SIGNAL_REMOTE_RECV = 0x14;

    public static final int GSE_SIGNAL_REQ_ADDR = 0x20;
}
