package stupaq.cloudatlas.messaging.messages;

import com.google.common.util.concurrent.SettableFuture;

import java.util.Iterator;
import java.util.List;

import stupaq.cloudatlas.messaging.Request;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.compact.CompactSerializable;
import stupaq.compact.TypeDescriptor;

public class EntitiesValuesRequest extends Request<SettableFuture<EntitiesValuesResponse>>
    implements Iterable<EntityName>, CompactSerializable {
  private final List<EntityName> entities;

  public EntitiesValuesRequest(List<EntityName> entities) {
    this.entities = entities;
  }

  @Override
  public Iterator<EntityName> iterator() {
    return entities.iterator();
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.EntitiesValuesRequest;
  }
}
