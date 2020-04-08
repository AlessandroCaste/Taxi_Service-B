package com.taxi.be.graph.engines;

import com.taxi.be.graph.elements.CityEdge;
import com.taxi.be.graph.elements.CityVertex;
import com.taxi.be.input.user.Taxi;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.function.Function;

public class CheapestPathCalculator {

    private SimpleWeightedGraph<CityVertex,CityEdge> grid;
    private ArrayList<Taxi> taxis;
    private CityVertex source;
    private CityVertex target;

    public CheapestPathCalculator(SimpleWeightedGraph<CityVertex,CityEdge> grid, ArrayList<Taxi> taxis, CityVertex source, CityVertex target) {
        this.grid = grid;
        this.taxis = taxis;
        this.source = source;
        this.target = target;
    }

    public GraphWalk<CityVertex,CityEdge> calculate() {
        DijkstraShortestPath<CityVertex, CityEdge> dijkstra_moneywise = new DijkstraShortestPath<>(grid);
        ArrayList<RoutesPrice> routesPrice = new ArrayList<>();
        HashMap<Taxi, GraphWalk<CityVertex,CityEdge>> cheapestPaths = new HashMap<>();

        for(Taxi taxi : taxis) {
            GraphWalk<CityVertex, CityEdge> path = (GraphWalk<CityVertex, CityEdge>) dijkstra_moneywise.getPath(taxi.getPositionAsCityVertex(), source);
            cheapestPaths.put(taxi,path);
            routesPrice.add(new RoutesPrice(taxi, path.getWeight()));
        }
        routesPrice.sort(Comparator.comparingDouble(taxiWeight -> taxiWeight.price));
        GraphWalk<CityVertex,CityEdge> cheapestPath = cheapestPaths.get(routesPrice.get(0).taxi);

        // Calculating the road from the user to the target
        GraphWalk<CityVertex,CityEdge> routini = (GraphWalk<CityVertex, CityEdge>) dijkstra_moneywise.getPath(source, target);
        // Joining the two parts of the route
        Function<GraphWalk<CityVertex,CityEdge>,Double> ciao = GraphWalk::getWeight;
        GraphWalk<CityVertex,CityEdge> totalRoute = cheapestPath.concat(routini,ciao);
        return totalRoute;
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
