/*
    "GraphSE" Graph Streaming Edge Processing Framework

    Copyright 2023, University of California, SC.
    Author: Yinyuan Zhao (yzhao321@ucsc.edu)

    All rights reserved.
 */

package ucsc.gse.scribe;

import java.util.*;

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

    // Worker computation
    GseGraph appLocalGraph = null;
    List<Integer> appRemoteList = null;
    HashMap<Topic, GseOperator> appLocalTopicOperator = new HashMap<>();
    boolean appLocalHalt = false;
    boolean appRemoteHalt = false;
    // Master management
    HashMap<NodeHandle, Boolean> appHaltMap = null;

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
        if (appLocalGraph == null) {
            return;
        }

        // Computation: y = f(x)
        int contentSignal = ((GseScribeContent)content).run(appLocalGraph);
        // Statemachine: sig --> action
        processSignal(contentSignal, (GseScribeContent)content);
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
        appRemoteList = appLocalGraph.aggregateRemoteList();
    }

    public void printGraph(String head) {
        if (appLocalGraph == null) {
            System.out.println(head + "---null---");
            return;
        }
        appLocalGraph.print(head + "   ");
    }

    /* **************************** State Machine ******************************* */
    private void processSignal(int contentSignal, GseScribeContent content) {
        switch (contentSignal) {
            case GseSignal.GSE_SIGNAL_LOCAL_HALT:
                appLocalHalt = true;
                if (appLocalHalt && appRemoteHalt) {
                    // appLocalEndpoint.route(null, new GseMsg(GseMsg.GSE_MSG_HALT, appLocalEndpoint.getLocalNodeHandle()), content.getSrc());
                    break;
                }

            case GseSignal.GSE_SIGNAL_LOCAL_PUB:
                appLocalHalt = false;
                GseGraph sendRomoteGraph = appLocalGraph.aggregate(appRemoteList);
                GseScribeContentRemote publishContent = new GseScribeContentRemote(
                    appLocalEndpoint.getLocalNodeHandle(), sendRomoteGraph, content.getTopic(), appLocalTopicOperator.get(content.getTopic())
                );
                appLocalScribe.publish(content.getTopic(), publishContent);
                break;

            case GseSignal.GSE_SIGNAL_REMOTE_HALT:
                appRemoteHalt = true;
                break;

            case GseSignal.GSE_SIGNAL_REMOTE_RECV:
                appRemoteHalt = false;
                break;

            default:
                break;
        }
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
}
