package com.taxi.be;

import com.taxi.be.graph.CityGraph;
import com.taxi.be.input.city.CityMap;
import com.taxi.be.input.user.UserRequest;
import com.taxi.be.repository.MapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class GraphsManager {

    @Autowired
    MapRepository mapRepository;

    public void setMapRepository(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    private HashMap<String, CityGraph> cityGraphHashMap = new HashMap<>();

    public void request(String city, UserRequest userRequest) throws Exception {
        CityGraph referencedGraph;
        if(cityGraphHashMap.containsKey(city))
            referencedGraph = cityGraphHashMap.get(city);
        else {
            Optional<CityMap> foundCityMap = mapRepository.findById(city);
            if(!foundCityMap.isPresent())
                throw new Exception();
            CityMap cityMap = foundCityMap.get();
            referencedGraph = new CityGraph(cityMap);
            cityGraphHashMap.put(city,referencedGraph);
        }
        referencedGraph.getLeastExpensivePath();
        referencedGraph.getShortestPath();
    }

}
