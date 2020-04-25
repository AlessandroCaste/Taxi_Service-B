package com.taxi.sb;

import com.taxi.sb.input.city.Checkpoint;
import com.taxi.sb.input.city.CityMap;
import com.taxi.sb.input.city.Wall;
import com.taxi.sb.input.user.Taxi;
import com.taxi.sb.repositories.MapRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
@ContextConfiguration(classes=Application.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class MapTestIntegrationController {

    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private GraphsManager graphsManager;

    @Autowired
    private MockMvc mvc;

    // Testing some previous requests through the controller
    @Test
    public void basicTestThroughController() throws Exception {

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

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/basic/user_requests/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"source\":{\"x\":1,\"y\":2},\"destination\":{\"x\":5,\"y\":2}}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        // Analyzing the answer
        String extractedResponse = mvcResult.getResponse().getContentAsString();
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
    public void noPathRequest() throws Exception {
        // The map with split into two unreachable chunks by its walls
        CityMap unsolvable = new CityMap("unsolvable", 10, 2);

        Wall wall1 = new Wall(3, 1, 4, 1);
        unsolvable.addWall(wall1);

        Wall wall2 = new Wall(3,2,4,2);
        unsolvable.addWall(wall2);

        Taxi taxi = new Taxi("taxi",1,2);
        unsolvable.addTaxi(taxi);

        mapRepository.save(unsolvable);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/unsolvable/user_requests/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"source\":{\"x\":1,\"y\":1},\"destination\":{\"x\":5,\"y\":1}}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString().contains("no path exists for the request"),is(true));
    }

}
