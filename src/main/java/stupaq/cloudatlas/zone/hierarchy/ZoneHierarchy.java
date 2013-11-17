package stupaq.cloudatlas.zone.hierarchy;

import com.google.common.base.Preconditions;

import java.util.HashMap;

import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.naming.Nameable;

public final class ZoneHierarchy<Payload extends Nameable> {
  private final HashMap<LocalName, ZoneHierarchy<Payload>> childZones = new HashMap<>();
  private final Payload payload;
  private ZoneHierarchy<Payload> parentZone;

  public ZoneHierarchy(Payload payload) {
    Preconditions.checkNotNull(payload);
    this.payload = payload;
    this.parentZone = null;
  }

  public Payload getPayload() {
    return payload;
  }

  public void rootAt(ZoneHierarchy<Payload> parent) {
    Preconditions.checkNotNull(parent);
    if (parentZone != null) {
      parentZone.childZones.remove(payload.localName());
    }
    parentZone = parent;
    parentZone.childZones.put(payload.localName(), this);
  }
}
