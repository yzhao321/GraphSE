/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.publiclib;

public class GseSignal {
    public static final int GSE_SIGNAL_VOID = 0;
    public static final int GSE_SIGNAL_LOCAL_HALT = 1;
    public static final int GSE_SIGNAL_LOCAL_PUB = 2;
    public static final int GSE_SIGNAL_REMOTE_HALT = 3;
    public static final int GSE_SIGNAL_REMOTE_RECV = 4;
}
