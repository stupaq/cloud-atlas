package stupaq.cloudatlas.messaging.messages;

import com.google.common.util.concurrent.SettableFuture;

import stupaq.cloudatlas.messaging.Request;
import stupaq.cloudatlas.naming.GlobalName;

public class DumpZoneRequest extends Request<SettableFuture<DumpZoneResponse>> {
  private final GlobalName zone;

  public DumpZoneRequest(GlobalName zone) {
    this.zone = zone;
    attach(SettableFuture.<DumpZoneResponse>create());
  }

  public GlobalName getZone() {
    return zone;
  }
}
