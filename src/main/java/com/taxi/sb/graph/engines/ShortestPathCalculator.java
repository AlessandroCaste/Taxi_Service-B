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

    public Solution calculate() throws NoPathException {
        LOGGER.debug("Shortest path calculation started for " + source.toString() + " -> " + target.toString());

        // Producing a weightless version of our graph in order to get the shortest path
        // Hashmap stores the paths (keys = taxis). Utility ArrayList is for quickly sorting out the best choice by speed and price
        DijkstraShortestPath<CityVertex, CityEdge> dijkstra_shortest = new DijkstraShortestPath<>(new AsUnweightedGraph<>(grid));
        HashMap<Taxi, GraphWalk<CityVertex,CityEdge>> paths = new HashMap<>();
        ArrayList<Route> routesLength = new ArrayList<>();

        for(Taxi taxi : taxis) {
            GraphWalk<CityVertex,CityEdge> shortPath = (GraphWalk<CityVertex, CityEdge>) dijkstra_shortest.getPath(taxi.getPosition(), source);
            paths.put(taxi,shortPath);
            double realWeight = shortPath.getEdgeList().stream().mapToDouble(e -> grid.getEdgeWeight(e)).sum();
            if(shortPath!=null)
                routesLength.add(new Route(taxi,shortPath.getLength(),realWeight));
        }
        routesLength.sort(Comparator.comparingInt( (Route route) -> route.length)
                                    .thenComparingDouble( route -> route.price));
        chosenTaxi = routesLength.get(0).taxi;
        GraphWalk<CityVertex,CityEdge> quickestPath = paths.get(chosenTaxi);
        GraphWalk<CityVertex,CityEdge> userToTarget = (GraphWalk<CityVertex, CityEdge>) dijkstra_shortest.getPath(source, target);

        // Joining the two parts of the route
        Function<GraphWalk<CityVertex,CityEdge>,Double> calculateTotalWeight = graph -> graph.getEdgeList().stream()
                                                                                                .mapToDouble((x) -> grid.getEdgeWeight(x))
                                                                                                .sum();
        // In case no path existed
        if(quickestPath == null || userToTarget == null) {
            LOGGER.error("No path exists for the chosen target and destination");
            throw new NoPathException();
        }
        // Solution wrapping
        GraphWalk<CityVertex,CityEdge> completeRoute = quickestPath.concat(userToTarget,calculateTotalWeight);

        LOGGER.debug("Shortest path calculation ended for " + source.toString() + " -> " + target.toString());
        return new Solution(quickestPath,completeRoute,chosenTaxi);
    }

    public Taxi getChosenTaxi() {
        return chosenTaxi;
    }

}
