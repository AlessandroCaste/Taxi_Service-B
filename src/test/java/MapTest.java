import com.taxi.Application;
import com.taxi.be.GraphsManager;
import com.taxi.be.input.city.Checkpoint;
import com.taxi.be.input.city.CityMap;
import com.taxi.be.input.city.Wall;
import com.taxi.be.input.user.Taxi;
import com.taxi.be.input.user.UserRequest;
import com.taxi.be.repository.MapRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
@ContextConfiguration(classes=Application.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class MapTest {

    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private GraphsManager graphsManager;

    // Basic test to verify path is correctly found for a simple map
    @Test
    public void basicTest() throws Exception {

        // Loading a custom map
        CityMap basicTest = new CityMap("basic", 6, 4);

        Wall wallTest = new Wall(2, 1, 3, 1);
        wallTest.setCityMap(basicTest);
        basicTest.addWall(wallTest);

        Checkpoint c1 = new Checkpoint(3, 1, 2, 2, 2);
        Checkpoint c2 = new Checkpoint(3, 2, 2, 3, 2);
        Checkpoint c3 = new Checkpoint(3, 3, 2, 4, 2);
        Checkpoint c4 = new Checkpoint(3, 4, 2, 5, 2);
        ArrayList<Checkpoint> checkpoints = new ArrayList<>(Arrays.asList(c1, c2, c3, c4));

        for (Checkpoint c : checkpoints) {
            c.setCityMap(basicTest);
            basicTest.addCheckpoint(c);
        }

        Taxi taxiTest = new Taxi("taxi",1,2);
        taxiTest.setCityMap(basicTest);
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
    public void noPathRequest() throws ExecutionException, InterruptedException {
        // The map with split into two unreachable chunks by its walls
        CityMap unsolvable = new CityMap("unsolvable", 10, 2);

        Wall wall1 = new Wall(3, 1, 4, 1);
        wall1.setCityMap(unsolvable);
        unsolvable.addWall(wall1);

        Wall wall2 = new Wall(3,2,4,2);
        wall2.setCityMap(unsolvable);
        unsolvable.addWall(wall2);

        Taxi taxi = new Taxi("taxi",1,2);
        taxi.setCityMap(unsolvable);
        unsolvable.addTaxi(taxi);

        mapRepository.save(unsolvable);

        Future<String> response = graphsManager.request(new UserRequest("unsolvable",1,1,5,1));
        assertThat(response.get().equals("no path can be retrieved"),is(true));
    }

}
