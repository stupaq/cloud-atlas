package stupaq.cloudatlas.messaging.messages;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.Response;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;

@Immutable
public class DumpZoneResponse extends Response<DumpZoneRequest> {
  private final ZoneManagementInfo zmi;

  public DumpZoneResponse(ZoneManagementInfo zmi) {
    this.zmi = zmi;
  }

  public ZoneManagementInfo getZmi() {
    return zmi;
  }
}
