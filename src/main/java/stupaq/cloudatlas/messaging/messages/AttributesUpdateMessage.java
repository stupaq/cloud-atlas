package stupaq.cloudatlas.messaging.messages;

import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.messaging.Message;
import stupaq.cloudatlas.naming.GlobalName;

@Immutable
public final class AttributesUpdateMessage extends Message implements Iterable<Attribute> {
  private final GlobalName zone;
  private final List<Attribute> attributes;
  private final boolean override;

  public AttributesUpdateMessage(GlobalName zone, List<Attribute> attributes, boolean override) {
    Preconditions.checkNotNull(zone);
    Preconditions.checkNotNull(attributes);
    this.zone = zone;
    this.attributes = attributes;
    this.override = override;
  }

  public GlobalName getZone() {
    return zone;
  }

  public boolean isOverride() {
    return override;
  }

  @Override
  public Iterator<Attribute> iterator() {
    return attributes.iterator();
  }
}
