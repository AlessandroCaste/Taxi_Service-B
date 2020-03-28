package com.taxi.be.graph;

import java.util.function.Supplier;

public class CityEdgeSupplier implements Supplier<CityEdge> {

    public CityEdge get() {
        return new CityEdge();
    }

}





