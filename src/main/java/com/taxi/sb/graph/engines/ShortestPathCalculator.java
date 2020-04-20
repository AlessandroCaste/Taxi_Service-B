package com.taxi.sb.graph.engines;

import com.taxi.sb.exceptions.NoPathException;
import com.taxi.sb.graph.CityGraph;
import com.taxi.sb.graph.elements.CityEdge;
import com.taxi.sb.graph.elements.CityVertex;
import com.taxi.sb.input.user.Taxi;
import com.taxi.sb.response.Solution;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AsUnweightedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class ShortestPathCalculator {

    private SimpleWeightedGraph<CityVertex,CityEdge> grid;
    private ArrayList<Taxi> taxis;
    private CityVertex source;
    private CityVertex target;
    private Taxi chosenTaxi;

    private static final Logger LOGGER = LoggerFactory.getLogger(CityGraph.class.getName());

    public ShortestPathCalculator(SimpleWeightedGraph<CityVertex,CityEdge> grid, ArrayList<Taxi> taxis, CityVertex source, CityVertex target) {
        this.grid = grid;
        this.taxis = taxis;
        this.source = source;
        this.target = target;
    }

    public Solution calculate() throws ExecutionException {
        LOGGER.debug("Cheapest path calculation started for " + source.toString() + " -> " + target.toString());

        // Producing a weightless version of our graph in order to safely get the shortest path
        // Hashmap stores the paths (keys = taxis). Utility ArrayList is for quickly sorting out the best choice
        DijkstraShortestPath<CityVertex, CityEdge> dijkstra_shortest = new DijkstraShortestPath<>(new AsUnweightedGraph<>(grid));
        HashMap<Taxi, GraphWalk<CityVertex,CityEdge>> paths = new HashMap<>();
        ArrayList<RoutesLength> routesLength = new ArrayList<>();

        for(Taxi taxi : taxis) {
            GraphWalk<CityVertex,CityEdge> shortPath = (GraphWalk<CityVertex, CityEdge>) dijkstra_shortest.getPath(taxi.getPositionAsCityVertex(), source);
            paths.put(taxi,shortPath);
            routesLength.add(new RoutesLength(taxi,shortPath.getLength()));
        }
        routesLength.sort(Comparator.comparingInt(taxi -> taxi.length));
        chosenTaxi = routesLength.get(0).taxi;
        GraphWalk<CityVertex,CityEdge> quickestPath = paths.get(chosenTaxi);

        // Calculating the road from the user to the target
        GraphWalk<CityVertex,CityEdge> userToTarget = (GraphWalk<CityVertex, CityEdge>) dijkstra_shortest.getPath(source, target);
        // Joining the two parts of the route
        Function<GraphWalk<CityVertex,CityEdge>,Double> calculateTotalWeight = graph -> graph.getEdgeList().stream()
                                                                                                .mapToDouble((x) -> grid.getEdgeWeight(x))
                                                                                                .sum();
        // In case no path existed
        if(quickestPath == null || userToTarget == null)
           throw new NoPathException();
        // Solution wrapping
        GraphWalk<CityVertex,CityEdge> completeRoute = quickestPath.concat(userToTarget,calculateTotalWeight);

        LOGGER.debug("Cheapest path calculation ended for " + source.toString() + " -> " + target.toString());
        return new Solution(quickestPath,completeRoute,chosenTaxi);
    }

    public Taxi getChosenTaxi() {
        return chosenTaxi;
    }

    // A utility class to track to order taxis by the length of their route
    private class RoutesLength {
        Taxi taxi;
        int length;

        RoutesLength(Taxi taxi, int length) {
            this.taxi = taxi;
            this.length = length;
        }
    }

}
