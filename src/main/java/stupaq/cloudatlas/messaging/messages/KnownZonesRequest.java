package stupaq.cloudatlas.messaging.messages;

import com.google.common.util.concurrent.SettableFuture;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.Request;

@Immutable
public class KnownZonesRequest extends Request<SettableFuture<KnownZonesResponse>> {
  public KnownZonesRequest() {
    attach(SettableFuture.<KnownZonesResponse>create());
  }
}
