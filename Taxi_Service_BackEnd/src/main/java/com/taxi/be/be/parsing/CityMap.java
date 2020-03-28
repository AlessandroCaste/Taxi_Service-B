package com.taxi.be.parsing;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name="City")
public class CityMap {

    @Id
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

    public CityMap(String cityId, int width, int height, List<Wall> walls, List<Checkpoint> checkpoints) {
        this.cityId = cityId;
        this.width = width;
        this.height = height;
        if(walls == null)
            walls = new ArrayList<>();
        this.walls = walls;
        if(checkpoints == null)
            checkpoints = new ArrayList<>();
        this.checkpoints = checkpoints;
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
        return "City = " + cityId + " width " + width + " height " + height;
    }

}
