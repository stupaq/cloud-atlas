package stupaq.cloudatlas.messaging.messages;

import java.util.List;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.messaging.Response;
import stupaq.cloudatlas.services.rmiserver.handler.LocalClientHandler.LocalClientResponse;

public class EntitiesValuesResponse extends Response<EntitiesValuesRequest>
    implements LocalClientResponse {
  private final List<Attribute> values;

  public EntitiesValuesResponse(List<Attribute> values) {
    this.values = values;
  }

  public List<Attribute> getList() {
    return values;
  }
}
