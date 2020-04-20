package com.taxi.sb.repositories;

import com.taxi.sb.input.city.CityMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapRepository extends JpaRepository<CityMap, String> {
}
