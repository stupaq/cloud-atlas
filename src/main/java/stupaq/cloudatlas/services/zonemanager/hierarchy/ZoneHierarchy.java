package stupaq.cloudatlas.services.zonemanager.hierarchy;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Hierarchical;
import stupaq.commons.base.Function1;
import stupaq.commons.base.Function2;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;

import static stupaq.compact.CompactSerializers.Collection;

public final class ZoneHierarchy<Payload extends Hierarchical> {
  private final HashMap<LocalName, ZoneHierarchy<Payload>> childZones = Maps.newHashMap();
  private Payload payload;
  private ZoneHierarchy<Payload> parentZone;

  public ZoneHierarchy(Payload payload) {
    Preconditions.checkNotNull(payload);
    this.payload = payload;
    this.parentZone = null;
  }

  public void walkUp(Synthesizer<Payload> action) {
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
    return find(resolveRelative(globalName));
  }

  private Iterator<LocalName> resolveRelative(GlobalName globalName) {
    Preconditions.checkState(parentZone == null && localName().equals(LocalName.getRoot()),
        "Resolving global name from non-root");
    Iterator<LocalName> suffix = globalName.iterator();
    assert suffix.next().equals(LocalName.getRoot());
    return suffix;
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

  public void modify(Modifier<Payload> action) {
    this.payload = action.apply(this.payload);
  }

  public void modifyAll(Modifier<Payload> action) {
    this.modify(action);
    for (ZoneHierarchy<Payload> child : childZones.values()) {
      child.modifyAll(action);
    }
  }

  public void synthesize(Synthesizer<Payload> action) {
    this.payload = action.apply(this.childZonesPayloads(), this.payload);
  }

  public void synthesizeFromLeaves(Synthesizer<Payload> action) {
    Set<ZoneHierarchy<Payload>> added = new HashSet<>();
    Queue<ZoneHierarchy<Payload>> queue =
        findLeaves().copyInto(new ArrayDeque<ZoneHierarchy<Payload>>());
    while (!queue.isEmpty()) {
      ZoneHierarchy<Payload> current = queue.remove();
      current.synthesize(action);
      if (current != this && current.parentZone != null && added.add(current.parentZone)) {
        queue.add(current.parentZone);
      }
    }
  }

  public <Result extends Hierarchical> ZoneHierarchy<Result> map(Function1<Payload, Result> fun) {
    ZoneHierarchy<Result> node = new ZoneHierarchy<>(fun.apply(getPayload()));
    for (ZoneHierarchy<Payload> child : childZones.values()) {
      child.map(fun).attachTo(node);
    }
    return node;
  }

  public void attachTo(ZoneHierarchy<Payload> parent) {
    Preconditions.checkNotNull(parent);
    if (parentZone != null) {
      parentZone.childZones.remove(payload.localName());
    }
    parentZone = parent;
    parentZone.childZones.put(payload.localName(), this);
  }

  public Payload insert(GlobalName globalName, Inserter<Payload> inserter) {
    inserter.descend(LocalName.getRoot());
    return insert(resolveRelative(globalName), inserter);
  }

  public Payload insert(Iterator<LocalName> relative, Inserter<Payload> inserter) {
    LocalName local = relative.next();
    ZoneHierarchy<Payload> child = childZones.get(local);
    if (child == null) {
      child = new ZoneHierarchy<>(inserter.create(local));
      child.attachTo(this);
    } else {
      inserter.descend(local);
    }
    return relative.hasNext() ? child.insert(relative, inserter) : child.getPayload();
  }

  public Payload getPayload() {
    return payload;
  }

  public GlobalName globalName() {
    final GlobalName.Builder builder = GlobalName.builder();
    walkUp(new InPlaceSynthesizer<Payload>() {
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

  private void appendTo(StringBuilder builder) {
    builder.append(globalName()).append('\n');
    builder.append(payload.toString()).append("\n\n");
    for (ZoneHierarchy<Payload> child : childZones.values()) {
      child.appendTo(builder);
    }
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    appendTo(result);
    return result.toString();
  }

  public static <Payload extends Hierarchical & CompactSerializable> CompactSerializer<ZoneHierarchy<Payload>> Serializer(
      final CompactSerializer<Payload> payloadSerializer) {
    return new CompactSerializer<ZoneHierarchy<Payload>>() {
      @Override
      public ZoneHierarchy<Payload> readInstance(ObjectInput in) throws IOException {
        ZoneHierarchy<Payload> node = new ZoneHierarchy<>(payloadSerializer.readInstance(in));
        for (ZoneHierarchy<Payload> zone : Collection(this).readInstance(in)) {
          zone.attachTo(node);
        }
        return node;
      }

      @Override
      public void writeInstance(ObjectOutput out, ZoneHierarchy<Payload> object)
          throws IOException {
        // ZoneHierarchy forms a tree, we are good to go with usual serialization schema
        payloadSerializer.writeInstance(out, object.payload);
        Collection(this).writeInstance(out, object.childZones.values());
      }
    };
  }

  public static interface Hierarchical extends LocallyNameable {
  }

  public static abstract class Synthesizer<Payload extends Hierarchical>
      extends Function2<Iterable<Payload>, Payload, Payload> {
  }

  public static abstract class InPlaceSynthesizer<Payload extends Hierarchical>
      extends Synthesizer<Payload> {
    @Override
    public Payload apply(Iterable<Payload> payloads, Payload payload) {
      process(payloads, payload);
      return payload;
    }

    protected abstract void process(Iterable<Payload> payloads, Payload payload);
  }

  public static abstract class Modifier<Payload extends Hierarchical>
      extends Function1<Payload, Payload> {
  }

  public static abstract class InPlaceModifier<Payload extends Hierarchical>
      extends Modifier<Payload> {
    @Override
    public Payload apply(Payload payload) {
      process(payload);
      return payload;
    }

    protected abstract void process(Payload payload);
  }

  public static abstract class Inserter<Payload extends Hierarchical> {
    public void descend(@SuppressWarnings("unused") LocalName root) {
      // Do nothing
    }

    public abstract Payload create(LocalName local);
  }
}
