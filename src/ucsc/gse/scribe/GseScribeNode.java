/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.scribe;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import rice.p2p.commonapi.*;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.ScribeMultiClient;
import rice.p2p.scribe.Topic;

import ucsc.gse.content.*;
import ucsc.gse.graph.*;
import ucsc.gse.operator.*;
import ucsc.gse.publiclib.*;

public class GseScribeNode implements Application, ScribeMultiClient {
    // Pastry for routing msg in P2P network
    Endpoint appLocalEndpoint;
    // Sribe for pub/sub sribe msg (content)
    Scribe appLocalScribe;
    // Topics subscribed
    HashSet<Topic> appLocalTopics = new HashSet<>();

    /* **************************** Worker computation ********************************* */
    // Worker graph storage
    GseGraph appLocalGraph = null;
    // Graph vertex that connected with other graph
    List<Integer> appRemoteList = null;
    // Operator map by topic
    ConcurrentHashMap<Topic, GseOperator> appLocalTopicOperator = new ConcurrentHashMap<>();
    // Double flag (local/remote) for halt
    boolean appLocalHalt = false;
    boolean appRemoteHalt = false;

    /* **************************** Master management ********************************* */
    // Halt map
    ConcurrentHashMap<NodeHandle, Boolean> appHaltMap = null;

    /* **************************** Const value ********************************* */
    public static final String GSE_ENDPOINT_NAME = "GseEndpointInstance";
    public static final String GSE_SCRIBE_NAME = "GseScribeInstance";

    public GseScribeNode(Node node) {
        appLocalEndpoint = node.buildEndpoint(this, GSE_ENDPOINT_NAME);
        appLocalEndpoint.register();
        appLocalScribe = new ScribeImpl(node, GSE_SCRIBE_NAME);
    }

    /* **************************** Scribe Pub/Sub ****************************** */
    public void publish(Topic topic, ScribeContent content) {
        appLocalScribe.publish(topic, content);
    }

    public void subscribe(Topic topic) {
        appLocalScribe.subscribe(topic, this, null, null);
    }

    /* **************************** Scribe Interface **************************** */
    @Override
    public boolean anycast(Topic topic, ScribeContent content) {
        return false;
    }

    @Override
    public void deliver(Topic topic, ScribeContent content) {
        if (!(content instanceof GseScribeContent)) {
            System.out.println(this.appLocalEndpoint.getLocalNodeHandle() + " recv error content: " + content);
            return;
        }
        GseScribeContent gseContent = (GseScribeContent)content;

        // Run content --> sig
        int contentSignal = processContent(gseContent);
        // Statemachine: sig --> action
        processSignal(contentSignal, gseContent.getTopic());
    }

    @Override
    public void childAdded(Topic topic, NodeHandle child) {
        return;
    }

    @Override
    public void childRemoved(Topic topic, NodeHandle child) {
        return;
    }

    @Override
    public void subscribeFailed(Topic topic) {
        return;
    }

    @Override
    public void subscribeFailed(Collection<Topic> topics) {
        return;
    }

    @Override
    public void subscribeSuccess(Collection<Topic> topics) {
        return;
    }

    /* **************************** Topic Utility ******************************* */
    public boolean isRoot(Topic topic) {
        return appLocalScribe.isRoot(topic);
    }

    public Collection<NodeHandle> getChildren(Topic topic) {
        return appLocalScribe.getChildrenOfTopic(topic);
    }

    public NodeHandle getParent(Topic topic) {
        return appLocalScribe.getParent(topic);
    }

    public boolean isRootOfTopics() {
        for (Topic topic : appLocalTopics) {
            if (isRoot(topic)) {
                return true;
            }
        }
        return false;
    }

    /* **************************** Graph Interface ***************************** */
    public void storeGraph(GseGraph graph) {
        appLocalGraph = graph;
        // Record the vertex that connected with other nodes for reducing local graph when sending
        appRemoteList = appLocalGraph.reduceRemoteList();
    }

    public void printGraph(String head) {
        if (appLocalGraph == null) {
            System.out.println(head + "---null---");
            return;
        }
        appLocalGraph.print(head + "   ");
    }

    /* **************************** Application Interface *********************** */
    @Override
    public void deliver(Id id, Message message) {
        return;
    }

    @Override
    public boolean forward(RouteMessage message) {
        return true;
    }

    @Override
    public void update(NodeHandle handle, boolean joined) {
        return;
    }

    /* **************************** Java Object Interface *********************** */
    public String toString() {
        return "[GseScribeNode (" + appLocalEndpoint.getId() + ")]";
    }

    /* **************************** State Machine ******************************* */
    private int processContent(GseScribeContent content) {
        if (content == null) {
            return GseSignal.GSE_SIGNAL_VOID;
        }

        int signal = GseSignal.GSE_SIGNAL_VOID;
        switch (content.getState()) {
            case GseState.GSE_STATE_INIT:
                signal = content.run(null);
                break;

            case GseState.GSE_STATE_COMP:
                if (appLocalGraph == null) {
                    break;
                }
                // Computation: y = f(x)
                signal =  content.run(appLocalGraph);
                break;

            default:
                break;
        }

        return signal;
    }

    private void processSignal(int contentSignal, Topic topic) {
        if (contentSignal == GseSignal.GSE_SIGNAL_VOID) {
            return;
        }
        if (!stateMachineFuncMap.containsKey(contentSignal)) {
            System.out.println("Error signal! [" + contentSignal + "]");
            return;
        }
        stateMachineFuncMap.get(contentSignal).apply(topic);
    }

    Map<Integer, Function<Topic, Boolean>> stateMachineFuncMap = new ConcurrentHashMap<>() {{
        put(GseSignal.GSE_SIGNAL_LOCAL_HALT,    topic -> procSigLocalHalt(topic));
        put(GseSignal.GSE_SIGNAL_LOCAL_PUB,     topic -> procSigLocalPub(topic));
        put(GseSignal.GSE_SIGNAL_REMOTE_HALT,   topic -> procSigRemoteHalt(topic));
        put(GseSignal.GSE_SIGNAL_REMOTE_RECV,   topic -> procSigRemoteReceive(topic));
        put(GseSignal.GSE_SIGNAL_REQ_ADDR,      topic -> procSigReqAddr(topic));
    }};

    private boolean procSigLocalHalt(Topic topic) {
        // Two flag for determining to halt
        if (appLocalHalt && appRemoteHalt) {
            return true;
        }
        appLocalHalt = true;
        sendLocalGraph(topic);
        return true;
    }

    private boolean procSigLocalPub(Topic topic) {
        appLocalHalt = false;
        sendLocalGraph(topic);
        return true;
    }

    private boolean procSigRemoteHalt(Topic topic) {
        appRemoteHalt = true;
        return true;
    }

    private boolean procSigRemoteReceive(Topic topic) {
        appLocalHalt = false;
        appRemoteHalt = false;
        return true;
    }

    private boolean procSigReqAddr(Topic topic) {
        return true;
    }

    private void sendLocalGraph(Topic topic) {
        // Only send the vertex connected with other node
        GseGraph sendRomoteGraph = appLocalGraph.reduce(appRemoteList);
        // Publish by setting local graph as remote graph of other node
        GseScribeContentComputationRemote publishContent = new GseScribeContentComputationRemote(
            appLocalEndpoint.getLocalNodeHandle(), sendRomoteGraph, topic, appLocalTopicOperator.get(topic), GseState.GSE_STATE_COMP
        );
        appLocalScribe.publish(topic, publishContent);
    }
}
