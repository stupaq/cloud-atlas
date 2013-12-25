package stupaq.cloudatlas.messaging.messages.requests;

import com.google.common.util.concurrent.SettableFuture;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.messages.Request;
import stupaq.cloudatlas.messaging.messages.responses.KnownZonesResponse;

@Immutable
public class KnownZonesRequest extends Request<SettableFuture<KnownZonesResponse>> {
  public KnownZonesRequest() {
    attach(SettableFuture.<KnownZonesResponse>create());
  }
}
