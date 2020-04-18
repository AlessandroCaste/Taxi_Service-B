package com.taxi.sb.graph.elements;

import java.util.function.Supplier;

public class CityEdgeSupplier implements Supplier<CityEdge> {

    public CityEdge get() {
        return new CityEdge();
    }

}





