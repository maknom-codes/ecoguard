package com.maknom.eco.guard.model.zone;

import com.maknom.eco.guard.model.geom.GeoJsonFeature;
import com.maknom.eco.guard.model.geom.GeoJsonFeatureCollection;


public interface ProtectedZoneService {

   GeoJsonFeature create(ProtectedZoneRequest protectedZoneRequest);

   GeoJsonFeatureCollection getAllZones();

   ProtectedZoneBean getById(Long protectedZ0neId);
}
