package com.taxi.be.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

public class CityEdge extends DefaultWeightedEdge {

    private boolean isWall;
    private boolean isCheckpoint;

    public boolean isWall() {
        return isWall;
    }

    public boolean isCheckpoint() {
        return isCheckpoint;
    }

}

