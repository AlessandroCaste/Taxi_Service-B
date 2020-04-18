package com.taxi.sb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxi.sb.exceptions.InvalidMapException;
import com.taxi.sb.graph.CityGraph;
import com.taxi.sb.response.Response;
import com.taxi.sb.response.Solution;
import com.taxi.sb.graph.elements.CityVertex;
import com.taxi.sb.input.city.CityMap;
import com.taxi.sb.input.user.UserRequest;
import com.taxi.sb.repository.MapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class GraphsManager {

    @Autowired
    MapRepository mapRepository;

    public void setMapRepository(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    @Async("threadPoolTaskExecutor")
    @Transactional
    public Future<String> request(UserRequest userRequest) {
        String jsonResponse;
        try {
            CityGraph cityGraph = produceGraph(userRequest);
            Response response = calculatePaths(userRequest.getSource(),userRequest.getDestination(),cityGraph);
            jsonResponse = processJson(response);
        } catch (Exception e ) {
            jsonResponse = "no path can be retrieved";
        }
        return new AsyncResult<>(jsonResponse);
    }

    private CityGraph produceGraph(UserRequest userRequest) throws InvalidMapException {
        Optional<CityMap> foundCityMap = mapRepository.findById(userRequest.getCityId());
        if(foundCityMap.isEmpty())
            throw new InvalidMapException();
        CityMap cityMap = foundCityMap.get();
        if(cityMap.getTaxis().isEmpty())
            throw new InvalidMapException();
        return new CityGraph(cityMap);
    }

    private Response calculatePaths(CityVertex source, CityVertex destination, CityGraph cityGraph) throws ExecutionException, InterruptedException {
        cityGraph.calculatePaths(source, destination);
        Solution quickSolution = cityGraph.getShortestPath();
        Solution cheapSolution = cityGraph.getCheapestPath();
        return new Response(quickSolution,cheapSolution);
    }

    private String processJson(Response response) throws JsonProcessingException {
        String jsonResponse = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response);
        if (jsonResponse == null)
            jsonResponse = "no response exists for the requested path";
        return  jsonResponse;
    }

}
