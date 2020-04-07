package com.taxi.be.repository;

import com.taxi.be.input.city.CityMap;
import com.taxi.be.input.city.Wall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WallRepository extends JpaRepository<Wall, String> {
    Optional<List<Wall>> findAllByCityMap(CityMap cityMap);
}
