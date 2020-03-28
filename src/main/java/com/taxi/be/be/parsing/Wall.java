package com.taxi.be.parsing;

import com.taxi.be.graph.CityVertex;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table (name="Wall")
public class Wall implements Serializable {

    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="wall_id",updatable = false)
    private long id;
    private int x1,y1,x2,y2;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private CityMap cityMap;

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public CityMap getCityMap() {
        return cityMap;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    public CityVertex getSourceCoordinates() {
        return new CityVertex(x1,y1);
    }

    public CityVertex getTargetCoordinates() {
        return new CityVertex(x2,y2);
    }

}
