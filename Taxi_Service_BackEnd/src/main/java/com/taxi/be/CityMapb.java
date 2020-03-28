package com.taxi.be;

import com.taxi.be.graph.*;
import com.taxi.be.parsing.Checkpoint;
import com.taxi.be.parsing.Wall;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.GridGraphGenerator;
import org.jgrapht.graph.AsUnweightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

public class CityMapb {

    private String city;
    private int width;
    private int height;
    private ArrayList<Wall> walls;
    private ArrayList<Checkpoint> checkpoints;
    private SimpleWeightedGraph<CityVertex,CityEdge> grid;

    public void createGraph(){

        // Building the city grid
        SimpleWeightedGraph<CityVertex,CityEdge> graph = new SimpleWeightedGraph(new CityVertexSupplier(height,width),new CityEdgeSupplier());

        GridGraphGenerator gridGraphGenerator = new GridGraphGenerator<CityVertex,CityEdge>(height,width);
        gridGraphGenerator.generateGraph(graph,null);

        // Adding checkpoints
       for (Checkpoint check : checkpoints) {
            CityEdge checkpointEdge = graph.getEdge(check.getSourceCoordinates(),check.getTargetCoordinates());
            graph.setEdgeWeight(checkpointEdge,check.getPrice());
        }

        // Removing walls
        /*for (Wall wall : walls) {
            CityEdge wallEdge = graph.getEdge(wall.getSourceCoordinates(),wall.getTargetCoordinates());
            graph.removeEdge(wallEdge);
        }*/

    }

    // Provides an unweighted view on the com.taxi.springboot.graph for shortest path search
    public DijkstraShortestPath<CityVertex,CityEdge> getShortestPath() {
        DijkstraShortestPath dijkstra_shortest = new DijkstraShortestPath(new AsUnweightedGraph(grid));
        System.out.println(dijkstra_shortest.getPath(new CityVertex(1,1),new CityVertex(8,10)));
        return dijkstra_shortest;
    }

    // The standard weighted com.taxi.springboot.graph to take money into account
    public DijkstraShortestPath<CityVertex,CityEdge> getLeastExpensivePath() {
        DijkstraShortestPath dijkstra_moneywise = new DijkstraShortestPath(grid);
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

}
