package com.taxi.be.graph;

import com.taxi.be.graph.elements.CityEdge;
import com.taxi.be.graph.elements.CityEdgeSupplier;
import com.taxi.be.graph.elements.CityVertex;
import com.taxi.be.graph.elements.CityVertexSupplier;
import com.taxi.be.graph.engines.CheapestPathCalculator;
import com.taxi.be.graph.engines.ShortestPathCalculator;
import com.taxi.be.input.city.Checkpoint;
import com.taxi.be.input.city.CityMap;
import com.taxi.be.input.city.Wall;
import com.taxi.be.input.user.Taxi;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.GridGraphGenerator;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Function;

public class CityGraph {

    private String city;
    private int width;
    private int height;
    private ArrayList<Wall> walls;
    private ArrayList<Checkpoint> checkpoints;
    private ArrayList<Taxi> taxis;
    private SimpleWeightedGraph<CityVertex, CityEdge> grid;

    private GraphWalk<CityVertex,CityEdge> shortestPath;
    private GraphWalk<CityVertex,CityEdge> cheapestPath;

    public CityGraph(CityMap cityMap) {
        this.city = cityMap.getCityId();
        this.width = cityMap.getWidth();
        this.height = cityMap.getHeight();
        this.walls = new ArrayList<>(cityMap.getWalls());
        this.checkpoints = new ArrayList<>(cityMap.getCheckpoints());
        this.taxis = new ArrayList<>(cityMap.getTaxis());
        createGraph();
    }

    public void createGraph(){
        // Building the city grid
        grid = new SimpleWeightedGraph<>(new CityVertexSupplier(height,width),new CityEdgeSupplier());
        GridGraphGenerator<CityVertex,CityEdge> gridGraphGenerator = new GridGraphGenerator<>(height,width);
        gridGraphGenerator.generateGraph(grid,null);

        // Adding checkpoints
       for (Checkpoint checkpoint : checkpoints) {
            CityEdge checkpointEdge = grid.getEdge(checkpoint.getSourceAsCityVertex(),checkpoint.getTargetCityVertex());
            grid.setEdgeWeight(checkpointEdge,checkpoint.getPrice());
        }

        // Walls are modelled as the lack an edge between two nodes
        for (Wall wall : walls) {
            CityEdge wallEdge = grid.getEdge(wall.getSourceAsCityVertex(),wall.getTargetAsCityVertex());
            grid.removeEdge(wallEdge);
        }

    }

    public void calculatePaths(CityVertex source, CityVertex target) throws InterruptedException, ExecutionException {
        ShortestPathCalculator shortestPathCalculator = new ShortestPathCalculator(grid,taxis,source,target);
        CheapestPathCalculator cheapestPathCalculator = new CheapestPathCalculator(grid,taxis,source,target);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Callable<GraphWalk<CityVertex,CityEdge>> r1 = shortestPathCalculator::calculate;
        Callable<GraphWalk<CityVertex,CityEdge>> r2 = cheapestPathCalculator::calculate;
        ArrayList<Callable<GraphWalk<CityVertex,CityEdge>>> callable = new ArrayList<>(Arrays.asList(r1,r2));
        ArrayList<Future<GraphWalk<CityVertex,CityEdge>>> results = new ArrayList<>(executor.invokeAll(callable,60, TimeUnit.SECONDS));
        shortestPath = results.get(0).get();
        cheapestPath = results.get(1).get();
        executor.shutdown();
    }

    // The standard weighted graph to take money into account
    public DijkstraShortestPath<CityVertex,CityEdge> getLeastExpensivePath(CityVertex source, CityVertex target) {
        DijkstraShortestPath<CityVertex,CityEdge> dijkstra_moneywise = new DijkstraShortestPath<>(grid);
        System.out.println(dijkstra_moneywise.getPath(new CityVertex(1,1),new CityVertex(8,10)));
        return dijkstra_moneywise;
    }



    // Grid can be exported to a basic dot com.taxi.springboot.graph
    public void dotExport(File path) throws IOException {
        Function<CityVertex,String> extractor = a -> ("node_" + a.toString())
                                                        .replace("(","")
                                                        .replace(")","")
                                                        .replace(",","_");
        DOTExporter dotExporter = new DOTExporter(extractor);
        dotExporter.exportGraph(grid, new FileWriter(path));
    }


    public String getCity() {
        return city;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Inner class to track and order taxis with the associated weight of their path
    private class TaxiWeight {
        Taxi taxi;
        double weight;

        TaxiWeight(Taxi taxi,double weight) {
            this.taxi = taxi;
            this.weight = weight;
        }
    }

}
