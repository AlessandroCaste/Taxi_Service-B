package com.taxi.be.graph;

import com.taxi.be.graph.elements.CityEdge;
import com.taxi.be.graph.elements.CityVertex;
import org.jgrapht.graph.GraphWalk;

public class Route {

    private GraphWalk<CityVertex, CityEdge> taxiToUserRoute;
    private GraphWalk<CityVertex,CityEdge> completeRoute;
    private int waitTime;
    private double waitPrice;
    private double totalPrice;

    public Route(GraphWalk<CityVertex,CityEdge> taxiToUserRoute, GraphWalk<CityVertex,CityEdge> completeRoute) {
        this.taxiToUserRoute = taxiToUserRoute;
        this.completeRoute = completeRoute;
        waitTime = (int) (taxiToUserRoute.getLength() / 0.83333);
        waitPrice = taxiToUserRoute.getWeight();
        totalPrice = completeRoute.getWeight();
    }

    public GraphWalk<CityVertex, CityEdge> getTaxiToUserRoute() {
        return taxiToUserRoute;
    }

    public GraphWalk<CityVertex, CityEdge> getCompleteRoute() {
        return completeRoute;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public double getWaitPrice() {
        return waitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

}
