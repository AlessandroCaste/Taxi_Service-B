package com.taxi.sb.controller;

import com.taxi.sb.GraphsManager;
import com.taxi.sb.input.user.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.Future;

@RestController
public class BackEndController {

    @Autowired
    GraphsManager graphsManager;

    @Value("${spring.application.name}")
    String appName;

    public void setGraphsManager(GraphsManager graphsManager) {
        this.graphsManager = graphsManager;
    }

    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute("appName",appName);
        return "Welcome to " + model.getAttribute("appName");
    }

    @RequestMapping(value = "{city}/user_requests/", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> computeRoute(@PathVariable(value = "city") String city, @Valid @RequestBody UserRequest userRequest)
    throws Exception {
        userRequest.setCityId(city);
        Future<String> futureResponse = graphsManager.request(userRequest);
        String response = futureResponse.get();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
