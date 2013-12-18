package stupaq.cloudatlas.messaging.messages;

import stupaq.cloudatlas.messaging.Response;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;

public class DumpZoneResponse extends Response<DumpZoneRequest> {
  private final ZoneManagementInfo zmi;

  public DumpZoneResponse(ZoneManagementInfo zmi) {
    this.zmi = zmi;
  }

  public ZoneManagementInfo getZmi() {
    return zmi;
  }
}
