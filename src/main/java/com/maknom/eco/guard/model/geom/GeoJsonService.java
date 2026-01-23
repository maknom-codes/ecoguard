package com.maknom.eco.guard.model.geom;

import com.maknom.eco.guard.model.incident.IncidentBean;
import com.maknom.eco.guard.model.zone.ProtectedZoneBean;

import java.util.List;

public interface GeoJsonService {

   GeoJsonFeatureCollection convertIncidentsToGeoJson(List<IncidentBean> incidents);

   GeoJsonFeatureCollection convertZonesToGeoJson(List<ProtectedZoneBean> zones);

   GeoJsonFeature<PointGeometry> convertToGeoJsonFeature(IncidentBean incidentBean);

   GeoJsonFeature<PolygonGeometry> convertToGeoJsonPolygonFeature(ProtectedZoneBean zoneBean);

}
