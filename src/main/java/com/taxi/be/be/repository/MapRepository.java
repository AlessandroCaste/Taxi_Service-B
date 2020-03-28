package com.taxi.be.repository;

import com.taxi.be.parsing.CityMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapRepository extends JpaRepository<CityMap,String> {
}
