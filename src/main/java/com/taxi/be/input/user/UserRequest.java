package com.taxi.be.input.user;

public class UserRequest {

    private String cityId;
    private Coordinate source;
    private Coordinate destination;

    public Coordinate getSource() {
        return source;
    }

    public Coordinate getDestination() {
        return destination;
    }

}
