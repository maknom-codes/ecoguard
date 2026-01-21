package com.maknom.eco.guard.controller;

import com.maknom.eco.guard.model.geom.GeoJsonFeatureCollection;
import com.maknom.eco.guard.model.zone.ProtectedZone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoGuardResponse {
   List<ProtectedZone> protectedZones;
   GeoJsonFeatureCollection incidentZones;
}
