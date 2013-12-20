package stupaq.cloudatlas.messaging.messages;

import java.util.Iterator;
import java.util.List;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.messaging.Response;
import stupaq.cloudatlas.services.rmiserver.handler.LocalClientHandler.LocalClientResponse;
import stupaq.compact.CompactSerializable;
import stupaq.compact.TypeDescriptor;

public class EntitiesValuesResponse extends Response<EntitiesValuesRequest>
    implements Iterable<AttributeValue>, CompactSerializable, LocalClientResponse {
  private final List<AttributeValue> values;

  public EntitiesValuesResponse(List<AttributeValue> values) {
    this.values = values;
  }

  @Override
  public Iterator<AttributeValue> iterator() {
    return values.iterator();
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.EntitiesValuesResponse;
  }
}
