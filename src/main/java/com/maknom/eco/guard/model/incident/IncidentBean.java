package com.maknom.eco.guard.model.incident;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;


@Entity(name = "incidents")
@Table(name = "incidents")
@Data
public class IncidentBean {


   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "zone_id")
   private Long zoneId;

   @Column(name = "user_id")
   private Long userId;

   private String category;

   private String description;

   @Enumerated(EnumType.STRING)
   private UrgencyType urgency;

   @Column(columnDefinition = "geometry(Point, 4326)")
   private Point geom;

   @Column(name = "report_date")
   private LocalDateTime reportDate;

}
