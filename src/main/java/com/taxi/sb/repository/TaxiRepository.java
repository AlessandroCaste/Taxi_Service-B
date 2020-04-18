package com.taxi.sb.repository;

import com.taxi.sb.input.city.CityMap;
import com.taxi.sb.input.user.Taxi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxiRepository extends JpaRepository<Taxi, String> {
    Optional<List<Taxi>> findAllByCityMap(CityMap cityMap);
}
