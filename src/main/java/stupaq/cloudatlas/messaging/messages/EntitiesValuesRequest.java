package stupaq.cloudatlas.messaging.messages;

import com.google.common.util.concurrent.SettableFuture;

import java.util.Iterator;
import java.util.List;

import stupaq.cloudatlas.messaging.Request;
import stupaq.cloudatlas.naming.EntityName;

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
