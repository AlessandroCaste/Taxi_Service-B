package com.taxi.sb.input.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taxi.sb.graph.elements.CityVertex;

import java.util.Map;

public class UserRequest {

    private String cityId;
    private int x1,y1,x2,y2;

    public UserRequest(String cityId,int x1,int y1,int x2,int y2) {
        this.cityId = cityId;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public CityVertex getSource() {
        return new CityVertex(x1,y1);
    }

    public CityVertex getDestination() {
        return new CityVertex(x2,y2);
    }


}
