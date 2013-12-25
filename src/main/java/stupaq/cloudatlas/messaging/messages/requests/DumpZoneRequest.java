package stupaq.cloudatlas.messaging.messages.requests;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.SettableFuture;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.messages.Request;
import stupaq.cloudatlas.messaging.messages.responses.DumpZoneResponse;
import stupaq.cloudatlas.naming.GlobalName;

@Immutable
public class DumpZoneRequest extends Request<SettableFuture<DumpZoneResponse>> {
  private final GlobalName zone;

  public DumpZoneRequest(GlobalName zone) {
    Preconditions.checkNotNull(zone);
    this.zone = zone;
    attach(SettableFuture.<DumpZoneResponse>create());
  }

  public GlobalName getZone() {
    return zone;
  }
}
