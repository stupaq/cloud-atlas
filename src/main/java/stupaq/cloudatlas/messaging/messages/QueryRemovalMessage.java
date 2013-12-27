package stupaq.cloudatlas.messaging.messages;

import com.google.common.base.Optional;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;

@Immutable
public class QueryRemovalMessage extends Message {
  private final Optional<AttributeName> name;
  private final Optional<List<GlobalName>> zones;

  public QueryRemovalMessage(Optional<AttributeName> name, Optional<List<GlobalName>> zones) {
    this.name = name;
    this.zones = zones;
  }

  public Optional<AttributeName> getName() {
    return name;
  }

  public Optional<List<GlobalName>> getZones() {
    return zones;
  }
}
