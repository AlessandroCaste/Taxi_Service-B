package com.taxi.be;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxi.be.graph.CityGraph;
import com.taxi.be.graph.Solution;
import com.taxi.be.input.city.CityMap;
import com.taxi.be.input.user.UserRequest;
import com.taxi.be.repository.MapRepository;
import com.taxi.be.repository.TaxiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.Future;

@Service
public class GraphsManager {

    @Autowired
    MapRepository mapRepository;

    @Autowired
    TaxiRepository taxiRepository;

    public void setMapRepository(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    @Async("threadPoolTaskExecutor")
    @Transactional
    public Future<String> request(String city, UserRequest userRequest) throws Exception {
        Optional<CityMap> foundCityMap = mapRepository.findById(city);
        if(foundCityMap.isEmpty())
            throw new NoResultException();
        CityMap cityMap = foundCityMap.get();
        CityGraph cityGraph = new CityGraph(cityMap);
        cityGraph.calculatePaths(userRequest.getSourceAsCityVertex(),userRequest.getDestinationAsCityVertex());
        Solution quickSolution = cityGraph.getShortestPath();
        Solution cheapSolution = cityGraph.getCheapestPath();
        Response response = new Response(quickSolution,cheapSolution);
        String jsonResponse = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response);
        return new AsyncResult<>(jsonResponse);
    }

}
