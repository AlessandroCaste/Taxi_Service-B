package com.taxi.sb.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ResponseSerializer extends StdSerializer<Response> {

    public ResponseSerializer() {
        this(null);
    }

    public ResponseSerializer(Class<Response> t) {
        super(t);
    }

    @Override
    public void serialize(Response response, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("quickest",response.getQuickest());
        jsonGenerator.writeObjectField("cheapest",response.getCheapest());
        jsonGenerator.writeEndObject();
    }
}
