package com.taxi.sb;

import com.taxi.sb.controller.ServiceController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ServiceController.class)
public class ControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GraphsManager graphsManager;

    // Testing controller correctly sets up
    @Test
    public void basicTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to Taxi Service B"))
                .andReturn();
    }

    // Testing a basic (wrong) request to verify the controller's correctly up
    @Test
    public void userRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/milan/user_requests")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content("{\"source\":{\"x\":1,\"y\":1},\"destination\":{\"x\":2,\"y\":\"2\"}"))
                .andExpect(status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

}