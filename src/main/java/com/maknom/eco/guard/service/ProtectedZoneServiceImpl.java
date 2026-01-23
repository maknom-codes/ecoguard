package com.maknom.eco.guard.service;

import com.maknom.eco.guard.model.geom.GeoJsonFeature;
import com.maknom.eco.guard.model.geom.GeoJsonFeatureCollection;
import com.maknom.eco.guard.model.geom.GeoJsonService;
import com.maknom.eco.guard.model.zone.ProtectedZoneBean;
import com.maknom.eco.guard.model.zone.ProtectedZoneRequest;
import com.maknom.eco.guard.model.zone.ProtectedZoneService;
import com.maknom.eco.guard.repository.ProtectedZoneRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;


@Service
public class ProtectedZoneServiceImpl implements ProtectedZoneService {

   private final ProtectedZoneRepository protectedZoneRepository;

   private final GeoJsonService geoJsonService;


   public ProtectedZoneServiceImpl(ProtectedZoneRepository protectedZoneRepository,
                                   GeoJsonService geoJsonService) {
      this.protectedZoneRepository = protectedZoneRepository;
      this.geoJsonService = geoJsonService;
   }


   @Override
   @Transactional
   @CacheEvict(value = "zone_list", allEntries = true)
   @CachePut(value = "zone_single", key = "#result.properties['id']")
   public GeoJsonFeature create(ProtectedZoneRequest protectedZoneRequest) {
      GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

      List<double[]> formattedCoords = protectedZoneRequest.getDataCoordinates()
              .stream()
              .map(list -> new double[]{list.get(0), list.get(1)})
              .toList();

      Coordinate[] coordinates = formattedCoords
              .stream()
              .map(p -> new Coordinate(p[0], p[1]))
              .toArray(Coordinate[]::new);

      if (!coordinates[0].equals2D(coordinates[coordinates.length - 1])) {
         Coordinate[] closedCoordinates = Arrays.copyOf(coordinates, coordinates.length + 1);
         closedCoordinates[closedCoordinates.length - 1] = closedCoordinates[0];
         coordinates = closedCoordinates;
      }

      LinearRing shell = geometryFactory.createLinearRing(coordinates);
      Polygon polygon = geometryFactory.createPolygon(shell, null);
      ProtectedZoneBean protectedZoneBean = new ProtectedZoneBean();
      protectedZoneBean.setName(protectedZoneRequest.getName());
      protectedZoneBean.setGeom(polygon);

      protectedZoneBean = protectedZoneRepository.save(protectedZoneBean);
      return geoJsonService.convertToGeoJsonPolygonFeature(protectedZoneBean);
   }

   @Override
   @Cacheable(value = "zone_list", key = "'all'")
   public GeoJsonFeatureCollection getAllZones() {
      List<ProtectedZoneBean> protectedZones = protectedZoneRepository.findAll();

      return geoJsonService.convertZonesToGeoJson(protectedZones);
   }

   @Override
   public ProtectedZoneBean getById(Long protectedZ0neId) {
      return protectedZoneRepository.findById(protectedZ0neId)
              .orElseThrow(() -> new RuntimeException("Zone not found"));
   }
}
