package com.taxi.sb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxi.sb.exceptions.InvalidMapException;
import com.taxi.sb.input.city.Checkpoint;
import com.taxi.sb.input.city.CityMap;
import com.taxi.sb.input.city.Wall;
import com.taxi.sb.input.user.Taxi;
import com.taxi.sb.input.user.UserRequest;
import com.taxi.sb.repositories.MapRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
@ContextConfiguration(classes=Application.class)
@AutoConfigureMockMvc
public class MapTest {

    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private GraphsManager graphsManager;

    @Autowired
    private MockMvc mvc;

    // Basic test to verify path is correctly found for a simple map
    @Test
    public void basicTest() throws Exception {

        // Loading a custom map
        CityMap basicTest = new CityMap("basic", 6, 4);

        Wall wallTest = new Wall(2, 1, 3, 1);
        basicTest.addWall(wallTest);

        Checkpoint c1 = new Checkpoint(3, 1, 2, 2, 2);
        Checkpoint c2 = new Checkpoint(3, 2, 2, 3, 2);
        Checkpoint c3 = new Checkpoint(3, 3, 2, 4, 2);
        Checkpoint c4 = new Checkpoint(3, 4, 2, 5, 2);
        ArrayList<Checkpoint> checkpoints = new ArrayList<>(Arrays.asList(c1, c2, c3, c4));

        for (Checkpoint c : checkpoints)
            basicTest.addCheckpoint(c);

        Taxi taxiTest = new Taxi("taxi",1,2);
        basicTest.addTaxi(taxiTest);

        mapRepository.save(basicTest);

        // Analyzing the answer
        Future<String> response = graphsManager.request(new UserRequest("basic", 1, 2, 5, 2));
        String extractedResponse = response.get();
        assertThat(extractedResponse.replaceAll("[\\n\\r\\s]", "").contains(
                "\"cost\":16.0," +
                        "\"distance\":4," +
                        "\"travelTime\":0.08," +
                        "\"waitTime\":0.0"
        ), is(true));
        assertThat(extractedResponse.replaceAll("[\\n\\r\\s]", "").contains(
                "\"cost\":6.0," +
                        "\"distance\":6," +
                        "\"travelTime\":0.12," +
                        "\"waitTime\":0.0"
        ), is(true));
    }

    // Map 2 has no valid path for the request
    @Test
    public void noPathRequest() {
        // The map with split into two unreachable chunks by its walls
        CityMap unsolvable = new CityMap("unsolvable", 10, 2);

        Wall wall1 = new Wall(3, 1, 4, 1);
        unsolvable.addWall(wall1);

        Wall wall2 = new Wall(3,2,4,2);
        unsolvable.addWall(wall2);

        Taxi taxi = new Taxi("taxi",1,2);
        unsolvable.addTaxi(taxi);

        mapRepository.save(unsolvable);

        assertThrows(ExecutionException.class, () -> {
             Future<String> result = graphsManager.request(new UserRequest("unsolvable",1,1,5,1));
             result.get();
        });
    }

    // Map 3 has many taxis to choose from
    @Test
    public void manyTaxis() throws ExecutionException, InterruptedException, InvalidMapException, JsonProcessingException {
        // The map with split into two unreachable chunks by its walls
        CityMap manyTaxis = new CityMap("manyTaxis", 12, 5);

        manyTaxis.addWall(new Wall(10, 5, 11, 5));
        manyTaxis.addWall(new Wall(11 ,5,12,5));


        manyTaxis.addCheckpoint(new Checkpoint(3,1,2,1,3));
        manyTaxis.addCheckpoint(new Checkpoint(3,2,2,2,3));
        manyTaxis.addCheckpoint(new Checkpoint(3,3,2,3,3));
        manyTaxis.addCheckpoint(new Checkpoint(3,3,2,4,2));
        manyTaxis.addCheckpoint(new Checkpoint(3,3,1,4,1));

        manyTaxis.addTaxi(new Taxi("taxi1",2,1));
        manyTaxis.addTaxi(new Taxi("taxi2",8,1));
        manyTaxis.addTaxi(new Taxi("taxi3",11,3));
        manyTaxis.addTaxi(new Taxi("taxi4",11,5));


        mapRepository.save(manyTaxis);

        // Verifying that the cheapest taxi with the shortest path is chosen
        Future<String> response1 = graphsManager.request(new UserRequest("manyTaxis",5,3,11,5));
        System.out.println(response1.get());

        assertThat(response1.get().contains("\"quickest\" : {\n" + "    \"taxi\" : \"taxi2\""),is(true));
        assertThat(response1.get().contains("\"cheapest\" : {\n" + "    \"taxi\" : \"taxi2\","),is(true));

        // Updating taxi1, giving an edge to it over the cheapest taxi
        manyTaxis.addTaxi(new Taxi("taxi1",3,1));
        mapRepository.save(manyTaxis);

        Future<String> response2 = graphsManager.request(new UserRequest("manyTaxis",5,3,11,5));
        assertThat(response2.get().contains("\"quickest\" : {\n" + "    \"taxi\" : \"taxi1\""),is(true));
        assertThat(response2.get().contains("\"cheapest\" : {\n" + "    \"taxi\" : \"taxi2\","),is(true));
    }

}
