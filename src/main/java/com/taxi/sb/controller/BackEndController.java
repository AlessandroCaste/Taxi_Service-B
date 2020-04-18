package com.taxi.sb.controller;

import com.taxi.sb.GraphsManager;
import com.taxi.sb.input.user.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.concurrent.Future;

@Controller
public class BackEndController {

    @Autowired
    GraphsManager graphsManager;

    public void setGraphsManager(GraphsManager graphsManager) {
        this.graphsManager = graphsManager;
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
