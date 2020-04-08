package com.taxi.be.repository;

import com.taxi.be.input.city.CityMap;
import com.taxi.be.input.user.Taxi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxiRepository extends JpaRepository<Taxi, String> {
    Optional<List<Taxi>> findAllByCityMap(CityMap cityMap);
}