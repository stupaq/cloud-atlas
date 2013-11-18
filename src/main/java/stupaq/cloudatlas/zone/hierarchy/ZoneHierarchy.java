package stupaq.cloudatlas.zone.hierarchy;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.naming.LocallyNameable;
import stupaq.cloudatlas.zone.hierarchy.ZoneHierarchy.Hierarchical;
import stupaq.guava.base.Function1;
import stupaq.guava.base.Function2;

public final class ZoneHierarchy<Payload extends Hierarchical> {
  private final HashMap<LocalName, ZoneHierarchy<Payload>> childZones = new HashMap<>();
  private Payload payload;
  private ZoneHierarchy<Payload> parentZone;

  public ZoneHierarchy(Payload payload) {
    Preconditions.checkNotNull(payload);
    this.payload = payload;
    this.parentZone = null;
  }

  public void walkUp(Aggregator<Payload> action) {
    ZoneHierarchy<Payload> current = this;
    while (current != null) {
      current.payload = action.apply(current.childZonesPayloads(), current.payload);
      current = current.parentZone;
    }
  }

  private Iterable<Payload> childZonesPayloads() {
    return FluentIterable.from(childZones.values())
        .transform(new Function<ZoneHierarchy<Payload>, Payload>() {
          @Override
          public Payload apply(ZoneHierarchy<Payload> zone) {
            return zone.payload;
          }
        });
  }

  public boolean isLeaf() {
    return childZones.isEmpty();
  }

  public Optional<ZoneHierarchy<Payload>> find(Iterator<LocalName> relative) {
    if (!relative.hasNext()) {
      return Optional.of(this);
    } else {
      ZoneHierarchy<Payload> child = childZones.get(relative.next());
      return child == null ? Optional.<ZoneHierarchy<Payload>>absent() : child.find(relative);
    }
  }

  public Optional<ZoneHierarchy<Payload>> find(GlobalName globalName) {
    Preconditions.checkState(parentZone == null && localName().equals(LocalName.getRoot()),
        "Resolving global name from non-root");
    Iterator<LocalName> suffix = globalName.iterator();
    assert suffix.next().equals(LocalName.getRoot());
    return find(suffix);
  }

  public FluentIterable<ZoneHierarchy<Payload>> findLeaves() {
    return FluentIterable.from(childZones.values()).transformAndConcat(
        new Function<ZoneHierarchy<Payload>, Iterable<? extends ZoneHierarchy<Payload>>>() {
          @Override
          public Iterable<? extends ZoneHierarchy<Payload>> apply(ZoneHierarchy<Payload> zone) {
            return zone.isLeaf() ? Collections.singletonList(zone) : zone.findLeaves();
          }
        });
  }

  public void map(Mapper<Payload> action) {
    this.payload = action.apply(this.payload);
  }

  public void mapAll(Mapper<Payload> action) {
    this.map(action);
    for (ZoneHierarchy<Payload> child : childZones.values()) {
      child.mapAll(action);
    }
  }

  public void zip(Aggregator<Payload> action) {
    this.payload = action.apply(this.childZonesPayloads(), this.payload);
  }

  public void zipFromLeaves(Aggregator<Payload> action) {
    Set<ZoneHierarchy<Payload>> added = new HashSet<>();
    Queue<ZoneHierarchy<Payload>> queue =
        findLeaves().copyInto(new ArrayDeque<ZoneHierarchy<Payload>>());
    while (!queue.isEmpty()) {
      ZoneHierarchy<Payload> current = queue.remove();
      current.zip(action);
      if (current != this && current.parentZone != null && added.add(current.parentZone)) {
        queue.add(current.parentZone);
      }
    }
  }

  public void rootAt(ZoneHierarchy<Payload> parent) {
    Preconditions.checkNotNull(parent);
    if (parentZone != null) {
      parentZone.childZones.remove(payload.localName());
    }
    parentZone = parent;
    parentZone.childZones.put(payload.localName(), this);
  }

  public Payload getPayload() {
    return payload;
  }

  public GlobalName globalName() {
    final GlobalName.Builder builder = GlobalName.builder();
    walkUp(new InPlaceAggregator<Payload>() {
      @Override
      protected void process(Iterable<Payload> payloads, Payload payload) {
        builder.parent(payload.localName());
      }
    });
    return builder.build();
  }

  private LocalName localName() {
    return payload.localName();
  }

  @Override
  public final int hashCode() {
    return super.hashCode();
  }

  @Override
  public final boolean equals(Object obj) {
    return super.equals(obj);
  }

  private void appendTo(final StringBuilder builder) {
    builder.append(globalName()).append('\n');
    builder.append(payload.toString()).append("\n\n");
    for (ZoneHierarchy<Payload> child : childZones.values()) {
      child.appendTo(builder);
    }
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder();
    appendTo(result);
    return result.toString();
  }

  public static interface Hierarchical extends LocallyNameable {
  }

  public static abstract class Aggregator<Payload extends Hierarchical>
      extends Function2<Iterable<Payload>, Payload, Payload> {
  }

  public static abstract class InPlaceAggregator<Payload extends Hierarchical>
      extends Aggregator<Payload> {
    @Override
    public Payload apply(Iterable<Payload> payloads, Payload payload) {
      process(payloads, payload);
      return payload;
    }

    protected abstract void process(Iterable<Payload> payloads, Payload payload);
  }

  public static abstract class Mapper<Payload extends Hierarchical>
      extends Function1<Payload, Payload> {
  }

  public static abstract class InPlaceMapper<Payload extends Hierarchical> extends Mapper<Payload> {
    @Override
    public Payload apply(Payload payload) {
      process(payload);
      return payload;
    }

    protected abstract void process(Payload payload);
  }
}
