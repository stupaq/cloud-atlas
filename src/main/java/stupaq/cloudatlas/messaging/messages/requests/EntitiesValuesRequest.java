package stupaq.cloudatlas.messaging.messages.requests;

import com.google.common.util.concurrent.SettableFuture;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.messages.responses.EntitiesValuesResponse;
import stupaq.cloudatlas.naming.EntityName;

@Immutable
public class EntitiesValuesRequest extends Request<SettableFuture<EntitiesValuesResponse>>
    implements Iterable<EntityName> {
  private final List<EntityName> entities;

  public EntitiesValuesRequest(List<EntityName> entities) {
    this.entities = entities;
    attach(SettableFuture.<EntitiesValuesResponse>create());
  }

  @Override
  public Iterator<EntityName> iterator() {
    return entities.iterator();
  }
}
