package stupaq.cloudatlas.messaging.messages.responses;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.messages.Response;
import stupaq.cloudatlas.messaging.messages.requests.DumpZoneRequest;
import stupaq.cloudatlas.services.rmiserver.handler.LocalClientHandler.LocalClientResponse;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;

@Immutable
public class DumpZoneResponse extends Response<DumpZoneRequest> implements LocalClientResponse {
  private final ZoneManagementInfo zmi;

  public DumpZoneResponse(ZoneManagementInfo zmi) {
    this.zmi = zmi;
  }

  public ZoneManagementInfo getZmi() {
    return zmi;
  }
}
