package com.taxi.be.parsing;

import com.taxi.be.graph.CityVertex;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(Checkpoint.class)
public class Checkpoint implements Serializable {
    private float price;

    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @ManyToOne
    @JoinColumn(name="cityId",nullable = false)
    private CityMap cityMap;
    private int x1,y1,x2,y2;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public CityMap getCityMap() {
        return cityMap;
    }

    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public CityVertex getSourceCoordinates() {
        return new CityVertex(x1,y1);
    }

    public CityVertex getTargetCoordinates() {
        return new CityVertex(x2,y2);
    }
}
