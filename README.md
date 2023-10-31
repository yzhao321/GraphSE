# GraphSE
A Framework for Graph Stream Edge Processing

## Architecture
The GraphSE engine code is in ./src/ucsc/gse

### Directory Structure
```
─ gse
|── client 	 (The entry of GraphSE engine)
|── content 	 (Publication message type among application tree)
|── graph 	 (Graph data struture)
|── msg 	 (Communication message)
|── operator 	 (Graph computation operator, e.g., PageRank)
|── publiclib 	 (Engine public define)
|── scribe 	 (Pub/Sub tree for communication of edge node)
|── simulator 	 (Simulator to run experiments with various config)
```

### Platform
```
|──────────────────────|
|       Graph SE       |      # Graph Processing
|   |──────────────|   |
|   |    Scribe    |   |      # Pub/Sub Pattern
|   |  |────────|  |   |
|   |  | Pastry |  |   |      # Edge Processing
|   |  |────────|  |   |
|   |──────────────|   |
|──────────────────────|
```

## Get Started
### Compile and Run
Compile
```
ant jar
```
Run
```
./run.sh
```

### Shell Client
Input "?" to show the cmd list
```
=================================================================
-----------------------------GraphSE-----------------------------
=================================================================

GraphSE:/>>?

=================================================================
Name			Description
-----------------------------------------------------------------
?			show cmd list
net			set network by: net [ip addr]
node			set node by: node [local] [extern]
input			set input by: input [fileName] [direction](0/1)
comp			add computation by: comp [treeName](CC/PR/SP)
deploy			start simulator
print			print scribe tree by: print [treeName]
step			trigger superstep step by step by: step [treeName]
launch			launch computation
group			print group result by: group [treeName]
max			print max result by: max [treeName]
=================================================================

GraphSE:/>>
```