# Taxi-Service-B

This service provides the second API interface for the 'Get My Taxi' challenge.  
Service B provides the following endpoint:

```
POST /process_requests/
```
Receives a json user request together with the map id.

## Further info

+ Map data is extracted from the db commonly shared with service A.
+ Maps are instantiated as a JgraphT grid, where walls are translated as edge removal and checkpoints as increased edge weights.
+ Each time a request is made a map is instantiated and the path retrieval process is started.
+ Graph management and path calculation have been parallelized. 
+ Dijkstra's shortest path algorithm has been used for path calculation (A* could be easily adopted as an alternative though).