package stupaq.cloudatlas.messaging.messages;

import com.google.common.base.Optional;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.values.CAQuery;
import stupaq.cloudatlas.naming.GlobalName;

@Immutable
public class QueryUpdateMessage extends Message {
  private final Attribute<CAQuery> query;
  private final Optional<List<GlobalName>> zones;

  public QueryUpdateMessage(Attribute<CAQuery> query, Optional<List<GlobalName>> zones) {
    this.query = query;
    this.zones = zones;
  }

  public Attribute<CAQuery> getQuery() {
    return query;
  }

  public Optional<List<GlobalName>> getZones() {
    return zones;
  }
}
