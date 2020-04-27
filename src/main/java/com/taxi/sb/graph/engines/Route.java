package com.taxi.sb.graph.engines;

import com.taxi.sb.input.user.Taxi;

class Route {
    Taxi taxi;
    int length;
    double price;

    Route(Taxi taxi, int length, double price) {
        this.taxi = taxi;
        this.length = length;
        this.price = price;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public int getLength() {
        return length;
    }

    public double getPrice() {
        return price;
    }
}
