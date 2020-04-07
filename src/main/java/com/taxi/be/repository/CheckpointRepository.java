package com.taxi.be.repository;

import com.taxi.be.input.city.Checkpoint;
import com.taxi.be.input.city.CityMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckpointRepository extends JpaRepository<Checkpoint, String> {
    Optional<List<Checkpoint>> findAllByCityMap(CityMap cityMap);
}
