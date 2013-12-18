package stupaq.cloudatlas.messaging.messages;

import com.google.common.util.concurrent.SettableFuture;

import stupaq.cloudatlas.messaging.Request;

public class KnownZonesRequest extends Request<SettableFuture<KnownZonesResponse>> {
  public KnownZonesRequest() {
    attach(SettableFuture.<KnownZonesResponse>create());
  }
}
