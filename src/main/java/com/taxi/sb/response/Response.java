package com.taxi.sb.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ResponseSerializer.class)
public class Response {

    private Solution quickest;
    private Solution cheapest;

    public Response(Solution quickest, Solution cheapest) {
        this.quickest = quickest;
        this.cheapest = cheapest;
    }

    public Solution getQuickest() {
        return quickest;
    }

    public Solution getCheapest()  {
        return cheapest;
    }

}
