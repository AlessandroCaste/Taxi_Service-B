package com.taxi.be.graph;

public class CityVertex {

    int x;
    int y;

    public CityVertex(int coordinate_x,int coordinate_y) {
        this.x = coordinate_x;
        this.y = coordinate_y;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CityVertex) && (toString().equals(o.toString()));
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

}
