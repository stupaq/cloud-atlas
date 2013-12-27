package stupaq.cloudatlas.messaging.messages.responses;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.messages.requests.KnownZonesRequest;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.rmiserver.handler.LocalClientHandler.LocalClientResponse;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;

@Immutable
public class KnownZonesResponse extends Response<KnownZonesRequest> implements LocalClientResponse {
  private final ZoneHierarchy<LocalName> zones;

  public KnownZonesResponse(ZoneHierarchy<LocalName> zones) {
    this.zones = zones;
  }

  public ZoneHierarchy<LocalName> getZones() {
    return zones;
  }
}
