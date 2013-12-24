package stupaq.cloudatlas.messaging.messages;

import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.messaging.Message;
import stupaq.cloudatlas.naming.GlobalName;

@Immutable
public final class AttributesUpdateMessage implements Iterable<Attribute>, Message {
  private final GlobalName zone;
  private final List<Attribute> attributes;

  public AttributesUpdateMessage(GlobalName zone, List<Attribute> attributes) {
    Preconditions.checkNotNull(zone);
    Preconditions.checkNotNull(attributes);
    this.zone = zone;
    this.attributes = attributes;
  }

  public GlobalName getZone() {
    return zone;
  }

  @Override
  public Iterator<Attribute> iterator() {
    return attributes.iterator();
  }
}
