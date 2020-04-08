package com.taxi.be;

import com.taxi.be.graph.CityGraph;
import com.taxi.be.graph.elements.CityEdge;
import com.taxi.be.graph.elements.CityVertex;
import com.taxi.be.input.city.CityMap;
import com.taxi.be.input.user.UserRequest;
import com.taxi.be.repository.MapRepository;
import com.taxi.be.repository.TaxiRepository;
import org.jgrapht.graph.GraphWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Optional;

@Service
public class GraphsManager {

    @Autowired
    MapRepository mapRepository;

    @Autowired
    TaxiRepository taxiRepository;

    public void setMapRepository(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    private HashMap<String, CityGraph> cityGraphHashMap = new HashMap<>();

    @Async("threadPoolTaskExecutor")
    @Transactional
    public void request(String city, UserRequest userRequest) throws Exception {CityGraph referencedGraph;
        if(cityGraphHashMap.containsKey(city))
            referencedGraph = cityGraphHashMap.get(city);
        else {
            Optional<CityMap> foundCityMap = mapRepository.findById(city);
            if(foundCityMap.isEmpty())
                throw new NoResultException();
            CityMap cityMap = foundCityMap.get();
            referencedGraph = new CityGraph(cityMap);
            cityGraphHashMap.put(city,referencedGraph);
        }
        referencedGraph.calculatePaths(userRequest.getSourceAsCityVertex(),userRequest.getDestinationAsCityVertex());
        GraphWalk<CityVertex,CityEdge> shortestPath = referencedGraph.getShortestPath();
        GraphWalk<CityVertex,CityEdge> cheapestPath = referencedGraph.getCheapestPath();
    }


}
