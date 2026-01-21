package com.maknom.eco.guard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maknom.eco.guard.model.zone.ProtectedZone;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProtectedZoneServiceImpl implements ProtectedZoneService {

   private final ProtectedZoneRepository protectedZoneRepository;

   private final ObjectMapper objectMapper;


   public ProtectedZoneServiceImpl(ProtectedZoneRepository protectedZoneRepository,
                                   ObjectMapper objectMapper) {
      this.protectedZoneRepository = protectedZoneRepository;
      this.objectMapper = objectMapper;
   }


   @Override
   @Transactional
   @CacheEvict(value = "zones", allEntries = true)
   public ProtectedZone create(ProtectedZoneRequest protectedZoneRequest) {
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

      protectedZoneBean = protectedZoneRepository.saveAndFlush(protectedZoneBean);
      ProtectedZone protectedZone;
      try {
         protectedZone = new ProtectedZone();
         protectedZone.setId(protectedZoneBean.getId());
         protectedZone.setName(protectedZone.getName());
         protectedZone.setGeoJson(objectMapper.writeValueAsString(protectedZoneBean.getGeom()));
      } catch (JsonProcessingException e) {
         throw new RuntimeException(e);
      }
      return protectedZone;
   }

   @Override
   @Cacheable(value = "zones", key = "#root.methodName")
   public List<ProtectedZone> getAllZones() {
      List<ProtectedZone> protectedZones = new ArrayList<>();
      protectedZones = protectedZoneRepository.findAll()
              .stream()
              .map(protectedZoneBean -> {
                 try {
                    ProtectedZone protectedZone = new ProtectedZone();
                    protectedZone.setId(protectedZoneBean.getId());
                    protectedZone.setName(protectedZoneBean.getName());
                    protectedZone.setGeoJson(objectMapper.writeValueAsString(protectedZoneBean.getGeom()));
                    return protectedZone;
                 } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                 }
              })
              .collect(Collectors.toList());
      return protectedZones;
   }

   @Override
   public ProtectedZoneBean getById(Long protectedZ0neId) {
      return protectedZoneRepository.findById(protectedZ0neId)
              .orElseThrow(() -> new RuntimeException("Zone not found"));
   }
}
