package com.taxi.be.graph;

import com.taxi.fe.users.Customer;
import com.taxi.fe.users.Taxi;

import java.util.ArrayList;
import java.util.function.Supplier;

public class CityVertexSupplier implements Supplier<CityVertex> {

    private int width;
    private int height;
    private int current_x = 1;
    private int current_y = 1;
    private ArrayList<Taxi> taxies;
    private ArrayList<Customer> customers;

    public CityVertexSupplier(int height, int width) {
        this.width = width;
        this.height = height;
    }

    public CityVertex get(){
        int coordinate_x;
        int coordinate_y;
        if(current_x > width &&  current_y < height) {
            coordinate_x = 1;
            coordinate_y = current_y + 1;
        }
        else {
            coordinate_x = current_x;
            coordinate_y = current_y;
        }
        current_x = coordinate_x + 1;
        current_y = coordinate_y;
        return new CityVertex(coordinate_x,coordinate_y);
    }

}
