package com.taxi.sb.input.city;

import com.taxi.sb.graph.elements.CityVertex;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="Wall")
public class Wall implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="wall_id",updatable = false)
    private long id;

    private int x1,y1,x2,y2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private CityMap cityMap;

    public Wall() {}

    public Wall(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

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

    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    public CityVertex getSourceAsCityVertex() {
        return new CityVertex(x1,y1);
    }

    public CityVertex getTargetAsCityVertex() {
        return new CityVertex(x2,y2);
    }

    public String coordinatesToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(x1).append(",").append(y1)
                .append(")->(")
                .append(x2).append(",").append(y2).append(")");
        return sb.toString();
    }

    @Override
    public String toString() {
        String cityId = "unassigned";
        if(cityMap.getCityId() != null)
            cityId = cityMap.getCityId();
        return "Wall in " + cityId + " | " + coordinatesToString();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Wall) && (toString().equals(o.toString()));
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
