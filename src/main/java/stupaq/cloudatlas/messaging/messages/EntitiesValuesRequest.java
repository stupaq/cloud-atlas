package stupaq.cloudatlas.messaging.messages;

import com.google.common.util.concurrent.SettableFuture;

import java.util.Iterator;
import java.util.List;

import stupaq.cloudatlas.messaging.Request;
import stupaq.cloudatlas.services.scribe.Entity;
import stupaq.compact.CompactSerializable;
import stupaq.compact.TypeDescriptor;

public class EntitiesValuesRequest extends Request<SettableFuture<EntitiesValuesResponse>>
    implements Iterable<Entity>, CompactSerializable {
  private final List<Entity> entities;

  public EntitiesValuesRequest(List<Entity> entities) {
    this.entities = entities;
  }

  @Override
  public Iterator<Entity> iterator() {
    return entities.iterator();
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.EntitiesValuesRequest;
  }
}
