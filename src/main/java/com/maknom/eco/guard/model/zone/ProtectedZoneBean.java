package com.maknom.eco.guard.model.zone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.locationtech.jts.geom.Polygon;


@Table(name = "protected_zones")
@Entity(name = "protected_zones")
@Data
public class ProtectedZoneBean {


   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String name;

   @Column(columnDefinition = "geometry(POLYGON, 4326")
   private Polygon geom;


}
