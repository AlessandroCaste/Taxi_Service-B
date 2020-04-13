package com.taxi.be.graph.engines;

import com.taxi.be.exceptions.NoPathException;
import com.taxi.be.graph.Solution;
import com.taxi.be.graph.elements.CityEdge;
import com.taxi.be.graph.elements.CityVertex;
import com.taxi.be.input.user.Taxi;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class CheapestPathCalculator {

    private SimpleWeightedGraph<CityVertex, CityEdge> grid;
    private ArrayList<Taxi> taxis;
    private CityVertex source;
    private CityVertex target;
    private Taxi chosenTaxi;

    public CheapestPathCalculator(SimpleWeightedGraph<CityVertex, CityEdge> grid, ArrayList<Taxi> taxis, CityVertex source, CityVertex target) {
        this.grid = grid;
        this.taxis = taxis;
        this.source = source;
        this.target = target;
    }

    public Solution calculate() throws ExecutionException {
        DijkstraShortestPath<CityVertex, CityEdge> dijkstra_moneywise = new DijkstraShortestPath<>(grid);
        ArrayList<RoutesPrice> routesPrice = new ArrayList<>();
        HashMap<Taxi, GraphWalk<CityVertex, CityEdge>> cheapestPaths = new HashMap<>();

        for (Taxi taxi : taxis) {
            GraphWalk<CityVertex, CityEdge> path = (GraphWalk<CityVertex, CityEdge>) dijkstra_moneywise.getPath(taxi.getPositionAsCityVertex(), source);
            cheapestPaths.put(taxi, path);
            routesPrice.add(new RoutesPrice(taxi, path.getWeight()));
        }
        routesPrice.sort(Comparator.comparingDouble(taxiWeight -> taxiWeight.price));
        chosenTaxi = routesPrice.get(0).taxi;
        GraphWalk<CityVertex, CityEdge> cheapestPath = cheapestPaths.get(chosenTaxi);

        // Calculating the road from the user to the target
        GraphWalk<CityVertex, CityEdge> userToTarget = (GraphWalk<CityVertex, CityEdge>) dijkstra_moneywise.getPath(source, target);
        Function<GraphWalk<CityVertex,CityEdge>,Double> calculateTotalWeight = graph -> graph.getEdgeList().stream()
                                                                                        .mapToDouble((x) -> grid.getEdgeWeight(x))
                                                                                        .sum();
        // In case no path existed
        if(cheapestPath == null || userToTarget == null)
            throw new NoPathException();
        // Solution wrapping
        GraphWalk<CityVertex,CityEdge> completeRoute = cheapestPath.concat(userToTarget, calculateTotalWeight);
        return new Solution(cheapestPath,completeRoute,chosenTaxi);
    }

    public Taxi getChosenTaxi() {
        return chosenTaxi;
    }

    // A utility class to track to order taxis by the length of their route
    private class RoutesPrice {
        Taxi taxi;
        double price;

        RoutesPrice(Taxi taxi, double price) {
            this.taxi = taxi;
            this.price = price;
        }
    }

}