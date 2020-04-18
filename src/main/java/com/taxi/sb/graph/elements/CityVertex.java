package com.taxi.sb.graph.elements;

public class CityVertex {

    int x;
    int y;

    public CityVertex(int x,int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
