package com.taxi.be.input.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taxi.be.graph.elements.CityVertex;

import java.util.Map;

public class UserRequest {

    private String cityId;
    private int x1,y1,x2,y2;

    @JsonProperty("source")
    public void parseSource(Map<String,Integer> source) {
        this.x1 = source.get("x");
        this.y1 = source.get("y");
    }

    @JsonProperty("destination")
    public void parseDestination(Map<String,Integer> destination) {
        this.x2 = destination.get("x");
        this.y2 = destination.get("y");
    }

    public CityVertex getSourceAsCityVertex() {
        return new CityVertex(x1,y1);
    }

    public CityVertex getDestinationAsCityVertex() {
        return new CityVertex(x2,y2);
    }

}
