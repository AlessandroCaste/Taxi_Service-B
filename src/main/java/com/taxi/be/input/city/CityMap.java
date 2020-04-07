package com.taxi.be.input.city;

import com.taxi.be.input.user.Taxi;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="City")
public class CityMap {

    @Id
    @NotNull
    @Column(name="city_id")
    private String cityId;
    private int width;
    private int height;
    @OneToMany(
            mappedBy = "cityMap",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Wall> walls;
    @OneToMany(
            mappedBy = "cityMap",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Checkpoint> checkpoints;
    @OneToMany(
            mappedBy = "cityMap",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Taxi> taxis;

    public CityMap() {}

    public CityMap(String cityId, int width, int height) {
        this.cityId = cityId;
        this.width = width;
        this.height = height;
        walls = new ArrayList<>();
        checkpoints = new ArrayList<>();
        taxis = new ArrayList<>();
    }

    public String getCityId() {
        return cityId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addWall(Wall wall) {
        walls.add(wall);
        wall.setCityMap(this);
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
        checkpoint.setCityMap(this);
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public void addTaxi(Taxi taxi) {
        taxis.remove(taxi);
        taxis.add(taxi);
        taxi.setCityMap(this);
    }

    public List<Taxi> getTaxis() { return taxis; }

    public void clear() {
        walls.clear();
        checkpoints.clear();
        taxis.clear();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CityMap) && (cityId.toLowerCase().equals(((CityMap) o).getCityId().toLowerCase()));
    }

    @Override
    public int hashCode() {
        return cityId.hashCode();
    }

    @Override
    public String toString() {
        return "City = " + cityId + " width = " + width + " height = " + height;
    }

}
