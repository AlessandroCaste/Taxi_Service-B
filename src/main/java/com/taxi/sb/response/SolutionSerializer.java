package com.taxi.sb.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.taxi.sb.graph.elements.CityVertex;

import java.io.IOException;

public class SolutionSerializer extends StdSerializer<Solution>
{

    public SolutionSerializer() {
        this(null);
    }

    public SolutionSerializer(Class<Solution> t) {
        super(t);
    }

    @Override
    public void serialize(Solution solution, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("taxi",solution.getTaxiId());
        jsonGenerator.writeFieldName("route");
        jsonGenerator.writeStartArray();
        for(CityVertex cv : solution.getCompleteRoute().getVertexList()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("x", cv.getX());
            jsonGenerator.writeNumberField("y",cv.getY());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeNumberField("cost",solution.getCompletePrice());
        jsonGenerator.writeNumberField("distance",solution.getCompleteLength());
        jsonGenerator.writeNumberField("travelTime",solution.getCompleteTime());
        jsonGenerator.writeNumberField("waitTime",solution.getPartialTime());
        jsonGenerator.writeEndObject();
    }
}
