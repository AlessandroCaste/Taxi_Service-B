package com.taxi.sb.input.user;

import com.taxi.sb.graph.elements.CityVertex;
import com.taxi.sb.input.city.CityMap;

import javax.persistence.*;

@Entity
@Table(name="Taxi")
public class Taxi {

    @Id
    private String taxiId;
    private int x,y;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private CityMap cityMap;

    public Taxi() {}

    public Taxi(String taxiId, int x, int y) {
        this.taxiId = taxiId;
        this.x = x;
        this.y = y;
    }

    public CityVertex getPosition() {
        return new CityVertex(x,y);
    }

    public String getTaxiId() {
        return taxiId;
    }

    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Taxi) && (taxiId.toLowerCase().equals(((Taxi) o).getTaxiId().toLowerCase()));
    }

    @Override
    public int hashCode() {
        return taxiId.hashCode();
    }

}
