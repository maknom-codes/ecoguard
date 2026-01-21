package com.maknom.eco.guard.model.zone;

import java.util.List;

public interface ProtectedZoneService {

   ProtectedZone create(ProtectedZoneRequest protectedZoneRequest);

   List<ProtectedZone> getAllZones();

   ProtectedZoneBean getById(Long protectedZ0neId);
}
