package com.taxi.sb.graph.engines;

import com.taxi.sb.exceptions.NoPathException;
import com.taxi.sb.graph.CityGraph;
import com.taxi.sb.graph.elements.CityEdge;
import com.taxi.sb.graph.elements.CityVertex;
import com.taxi.sb.input.user.Taxi;
import com.taxi.sb.response.Solution;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Function;

public class CheapestPathCalculator {

    private SimpleWeightedGraph<CityVertex, CityEdge> grid;
    private ArrayList<Taxi> taxis;
    private CityVertex source;
    private CityVertex target;
    private Taxi chosenTaxi;

    private static final Logger LOGGER = LoggerFactory.getLogger(CityGraph.class.getName());

    public CheapestPathCalculator(SimpleWeightedGraph<CityVertex, CityEdge> grid, ArrayList<Taxi> taxis, CityVertex source, CityVertex target) {
        this.grid = grid;
        this.taxis = taxis;
        this.source = source;
        this.target = target;
    }

    public Solution calculate() throws NoPathException {
        LOGGER.debug("Cheapest path calculation started for " + source.toString() + " -> " + target.toString());

        // Hashmap stores the paths (keys = taxis). Utility ArrayList is for quickly sorting out the best choice
        DijkstraShortestPath<CityVertex, CityEdge> dijkstra_moneywise = new DijkstraShortestPath<>(grid);
        ArrayList<RoutesPrice> routesPrice = new ArrayList<>();
        HashMap<Taxi, GraphWalk<CityVertex, CityEdge>> cheapestPaths = new HashMap<>();

        for (Taxi taxi : taxis) {
            GraphWalk<CityVertex, CityEdge> cheapPath = (GraphWalk<CityVertex, CityEdge>) dijkstra_moneywise.getPath(taxi.getPosition(), source);
            cheapestPaths.put(taxi, cheapPath);
            if(cheapPath!=null)
                routesPrice.add(new RoutesPrice(taxi, cheapPath.getWeight()));
        }
        routesPrice.sort(Comparator.comparingDouble(taxiWeight -> taxiWeight.price));
        chosenTaxi = routesPrice.get(0).taxi;
        GraphWalk<CityVertex, CityEdge> cheapestPath = cheapestPaths.get(chosenTaxi);
        GraphWalk<CityVertex, CityEdge> userToTarget = (GraphWalk<CityVertex, CityEdge>) dijkstra_moneywise.getPath(source, target);
        Function<GraphWalk<CityVertex,CityEdge>,Double> calculateTotalWeight = graph -> graph.getEdgeList().stream()
                                                                                        .mapToDouble((x) -> grid.getEdgeWeight(x))
                                                                                        .sum();
        // In case no path existed
        if(cheapestPath == null || userToTarget == null) {
            LOGGER.error("No path exists for the chosen target and destination");
            throw new NoPathException();
        }
        // Solution wrapping
        GraphWalk<CityVertex,CityEdge> completeRoute = cheapestPath.concat(userToTarget, calculateTotalWeight);

        LOGGER.debug("Cheapest path calculation ended " + source.toString() + " -> " + target.toString());
        return new Solution(cheapestPath,completeRoute,chosenTaxi);
    }

    public Taxi getChosenTaxi() {
        return chosenTaxi;
    }

    // A utility class to track to order taxis by the price of their route only
    private class RoutesPrice {
        Taxi taxi;
        double price;

        RoutesPrice(Taxi taxi, double price) {
            this.taxi = taxi;
            this.price = price;
        }
    }

}