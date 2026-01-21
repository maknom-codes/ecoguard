package com.maknom.eco.guard.repository;

import com.maknom.eco.guard.model.zone.ProtectedZoneBean;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProtectedZoneRepository extends JpaRepository<ProtectedZoneBean, Long> {

   @Query("""
           SELECT z 
           FROM protected_zones z 
           WHERE ST_Within(:point, z.geom) = true
           """)
   Optional<ProtectedZoneBean> findZoneContainingPoint(@Param("point") Point point);

   @Query("""
           SELECT z
           FROM protected_zones z 
           WHERE z.id =:id 
           """)
   Optional<ProtectedZoneBean> findById(@Param("id") Long id);
}
