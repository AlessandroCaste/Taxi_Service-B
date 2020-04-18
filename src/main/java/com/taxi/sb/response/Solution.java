package com.taxi.sb.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.taxi.sb.graph.elements.CityEdge;
import com.taxi.sb.graph.elements.CityVertex;
import com.taxi.sb.input.user.Taxi;
import org.jgrapht.graph.GraphWalk;

@JsonSerialize(using = SolutionSerializer.class)
public class Solution {

    private GraphWalk<CityVertex, CityEdge> partialRoute;
    private GraphWalk<CityVertex,CityEdge> completeRoute;
    private Taxi chosenTaxi;
    private int partialLength;
    private int completeLength;
    private double partialPrice;
    private double completePrice;
    private float partialTime;
    private float completeTime;

    public Solution(GraphWalk<CityVertex, CityEdge> partialRoute, GraphWalk<CityVertex,CityEdge> completeRoute, Taxi taxi) {
        this.partialRoute = partialRoute;
        this.completeRoute = completeRoute;
        this.chosenTaxi = taxi;
        this.partialLength = partialRoute.getLength();
        this.completeLength = completeRoute.getLength();
        this.partialPrice = completeRoute.getWeight();
        this.completePrice = completeRoute.getWeight();
        this.partialTime =  (float) (partialLength / 50.0);
        this.completeTime = (float) (completeLength / 50.0);
    }

    public GraphWalk<CityVertex, CityEdge> getPartialRoute() {
        return partialRoute;
    }

    public GraphWalk<CityVertex, CityEdge> getCompleteRoute() {
        return completeRoute;
    }

    public Taxi getChosenTaxi() {
        return chosenTaxi;
    }

    public String getTaxiId() {
        return chosenTaxi.getTaxiId();
    }

    public int getPartialLength() {
        return partialLength;
    }

    public int getCompleteLength() {
        return completeLength;
    }

    public double getPartialPrice() {
        return partialPrice;
    }

    public double getCompletePrice() {
        return completePrice;
    }

    public float getPartialTime() {
        return partialTime;
    }

    public float getCompleteTime() {
        return completeTime;
    }
}
