package com.maknom.eco.guard.service;

import com.maknom.eco.guard.model.incident.IncidentBean;
import com.maknom.eco.guard.model.incident.IncidentRequest;
import com.maknom.eco.guard.model.incident.IncidentService;
import com.maknom.eco.guard.model.incident.SyncDataRequest;
import com.maknom.eco.guard.model.incident.UrgencyType;
import com.maknom.eco.guard.model.user.UserBean;
import com.maknom.eco.guard.model.zone.ProtectedZoneBean;
import com.maknom.eco.guard.repository.IncidentRepository;
import com.maknom.eco.guard.repository.ProtectedZoneRepository;
import com.maknom.eco.guard.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class IncidentServiceImpl implements IncidentService {


   private final IncidentRepository incidentRepository;

   private final UserRepository userRepository;


   private final ProtectedZoneRepository protectedZoneRepository;

   public IncidentServiceImpl(ProtectedZoneRepository protectedZoneRepository,
                              UserRepository userRepository,
                              IncidentRepository incidentRepository) {
      this.incidentRepository = incidentRepository;
      this.userRepository = userRepository;
      this.protectedZoneRepository = protectedZoneRepository;
   }


   @Override
   @Transactional
   public IncidentBean create(String username, IncidentRequest incidentRequest) {

      GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
      Point position = geometryFactory.createPoint(new Coordinate(incidentRequest.getLongitude(), incidentRequest.getLatitude()));

      ProtectedZoneBean protectedZoneBean = protectedZoneRepository.findZoneContainingPoint(position)
              .orElseThrow(() -> new RuntimeException("Incident is over a protected zone"));

      UserBean userBean = userRepository.findByEmail(username).get();

      IncidentBean incidentBean = new IncidentBean();
      incidentBean.setDescription(incidentRequest.getDescription());
      incidentBean.setCategory(incidentRequest.getCategory());
      incidentBean.setUrgency(UrgencyType.parse(incidentRequest.getUrgency()));
      incidentBean.setGeom(position);
      incidentBean.setUserId(userBean.getId());
      incidentBean.setZoneId(protectedZoneBean.getId());
      incidentBean.setReportDate(LocalDateTime.now());
      return incidentRepository.save(incidentBean);
   }

   @Override
   @Transactional
   public IncidentBean create(String username, SyncDataRequest syncDataRequest) {

      GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
      Point position = geometryFactory.createPoint(new Coordinate(syncDataRequest.getIncidentRequest().getLongitude(),
              syncDataRequest.getIncidentRequest().getLatitude()));

      ProtectedZoneBean protectedZoneBean = protectedZoneRepository.findZoneContainingPoint(position)
              .orElseThrow(() -> new RuntimeException("Incident is over a protected zone"));

      UserBean userBean = userRepository.findByEmail(username).get();
      DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      IncidentBean incidentBean = new IncidentBean();
      incidentBean.setDescription(syncDataRequest.getIncidentRequest().getDescription());
      incidentBean.setCategory(syncDataRequest.getIncidentRequest().getCategory());
      incidentBean.setUrgency(UrgencyType.parse(syncDataRequest.getIncidentRequest().getUrgency()));
      incidentBean.setGeom(position);
      incidentBean.setUserId(userBean.getId());
      incidentBean.setZoneId(protectedZoneBean.getId());
      incidentBean.setReportDate(LocalDateTime.parse(syncDataRequest.getTimestamp(), formatter));
      return incidentRepository.save(incidentBean);
   }

   @Override
   public List<IncidentBean> getAllIncidents() {
      return incidentRepository.findAllIncidents();
   }

   @Override
   public IncidentBean getById(Long incidentId) {
      return incidentRepository.findById(incidentId).orElse(null);
   }
}
