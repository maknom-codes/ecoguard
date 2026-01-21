package com.maknom.eco.guard.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;


public class GeoJsonPolygonSerializer extends StdSerializer<Polygon> {


   public GeoJsonPolygonSerializer() {
      super(Polygon.class);
   }

   @Override
   public void serialize(Polygon value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeStartObject();
      gen.writeStringField("type", "Polygon");
      gen.writeFieldName("coordinates");
      gen.writeStartArray();
      gen.writeStartArray();
      for (Coordinate coordinate: value.getCoordinates()) {
         gen.writeStartArray();
         gen.writeNumber(coordinate.x);
         gen.writeNumber(coordinate.y);
         gen.writeEndArray();
      }
      gen.writeEndArray();
      gen.writeEndArray();
      gen.writeEndObject();
   }

}
