package com.maknom.eco.guard.model.geom;

import com.maknom.eco.guard.model.incident.IncidentBean;

import java.util.List;

public interface GeoJsonService {

   GeoJsonFeatureCollection convertIncidentsToGeoJson(List<IncidentBean> incidents);
}
