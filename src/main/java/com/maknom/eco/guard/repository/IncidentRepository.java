package com.maknom.eco.guard.repository;

import com.maknom.eco.guard.model.incident.IncidentBean;
import com.maknom.eco.guard.model.incident.IncidentZone;
import com.maknom.eco.guard.model.user.UserBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IncidentRepository extends JpaRepository<IncidentBean, Long> {


   @Query("""
           SELECT i
           FROM incidents i 
           WHERE i.id =:id 
           """)
   Optional<IncidentBean> findById(@Param("id") Long id);


//   @Query(value = """
//    SELECT
//        id,
//        description,
//        category,
//        report_date as reportDate,
//        urgency,
//        ST_AsGeoJSON(geom) as geometry,
//        zone_id as zoneId,
//        user_id as userId
//    FROM incidents
//    """, nativeQuery = true)
//   List<IncidentZone> findAllIncidents();

   @Query("SELECT i FROM incidents i ORDER BY i.reportDate DESC")
   List<IncidentBean> findAllIncidents();



//   @Query(value = """
//          SELECT json_build_object(
//               'type', 'FeatureCollection',
//               'features', jsonb_agg(feature)
//          )
//          FROM (
//            SELECT jsonb_build_object(
//               'type',     'Feature; ',
//               'id',          id,
//               'geometry',    ST_AsGeoJSON(geom)::jsonb,
//               'properties',  jsonb_build_object(
//                   'category',      category,
//                   'description',   description,
//                   'urgency',       urgency,
//                   'reportDate',    report_date,
//                   'zoneId',        zone_id,
//                   'userId',        user_id
//               )
//            ) AS feature FROM incidents
//          ) AS inputs
//           """, nativeQuery = true)
//   List<IncidentZone> findAllIncidents();
}
