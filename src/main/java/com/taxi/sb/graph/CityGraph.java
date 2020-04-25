package com.taxi.sb.graph;

import com.taxi.sb.graph.elements.CityEdge;
import com.taxi.sb.graph.elements.CityEdgeSupplier;
import com.taxi.sb.graph.elements.CityVertex;
import com.taxi.sb.graph.elements.CityVertexSupplier;
import com.taxi.sb.graph.engines.CheapestPathCalculator;
import com.taxi.sb.graph.engines.ShortestPathCalculator;
import com.taxi.sb.input.city.Checkpoint;
import com.taxi.sb.input.city.CityMap;
import com.taxi.sb.input.city.Wall;
import com.taxi.sb.input.user.Taxi;
import com.taxi.sb.response.Solution;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgrapht.generate.GridGraphGenerator;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Function;

public class CityGraph {

    private String cityId;
    private int width;
    private int height;
    private ArrayList<Wall> walls;
    private ArrayList<Checkpoint> checkpoints;
    private ArrayList<Taxi> taxis;
    private SimpleWeightedGraph<CityVertex, CityEdge> grid;

    private Solution quickSolution;
    private Solution cheapSolution;

    private static final Logger LOGGER = LoggerFactory.getLogger(CityGraph.class.getName());

    public CityGraph(CityMap cityMap) {
        this.cityId = cityMap.getCityId();
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
            CityEdge checkpointEdge = grid.getEdge(checkpoint.getSource(),checkpoint.getTarget());
            grid.setEdgeWeight(checkpointEdge,checkpoint.getPrice());
        }

        // Walls are modelled as the lack an edge between two nodes
        for (Wall wall : walls) {
            CityEdge wallEdge = grid.getEdge(wall.getSource(),wall.getTarget());
            grid.removeEdge(wallEdge);
        }

        LOGGER.debug("City graph " + cityId + " has been successfully created");
    }

    // Two classes handle the two different kind of searches, and two threads are spawned accordingly
    public void calculatePaths(CityVertex source, CityVertex target) throws InterruptedException, ExecutionException {
        ShortestPathCalculator shortestPathCalculator = new ShortestPathCalculator(grid,taxis,source,target);
        CheapestPathCalculator cheapestPathCalculator = new CheapestPathCalculator(grid,taxis,source,target);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Callable<Solution> r1 = shortestPathCalculator::calculate;
        Callable<Solution> r2 = cheapestPathCalculator::calculate;
        ArrayList<Callable<Solution>> callable = new ArrayList<>(Arrays.asList(r1,r2));
        ArrayList<Future<Solution>> results = new ArrayList<>(executor.invokeAll(callable,60, TimeUnit.SECONDS));
        quickSolution = results.get(0).get();
        cheapSolution = results.get(1).get();
        executor.shutdown();
    }

    public Solution getShortestPath() {
        return quickSolution;
    }

    public Solution getCheapestPath() {
        return cheapSolution;
    }

    // Testing/debug method to check graph construction
    public void export() {
        Function<CityVertex,String> nameExtractor = (x) -> x.toString().replaceAll("[(),]","");
        DOTExporter<CityVertex,CityEdge> exporter = new DOTExporter<>(nameExtractor);
        try {
            FileWriter writer = new FileWriter("src/test/output/grid.dot");
            exporter.exportGraph(grid,writer);
        } catch (IOException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
