package stupaq.cloudatlas.zone.hierarchy;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.naming.Nameable;
import stupaq.guava.base.Function2;

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

  public <Result> List<Result> walkUp(Function2<Iterable<Payload>, Payload, Result> action) {
    List<Result> results = new ArrayList<>();
    ZoneHierarchy<Payload> current = this;
    while (current != null) {
      results.add(action.apply(FluentIterable.from(childZones.values())
          .transform(new Function<ZoneHierarchy<Payload>, Payload>() {
            @Override
            public Payload apply(ZoneHierarchy<Payload> zone) {
              return zone.getPayload();
            }
          }), payload));
      current = current.parentZone;
    }
    return results;
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
