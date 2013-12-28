package stupaq.cloudatlas.services.zonemanager.hierarchy;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.naming.GlobalName.Builder;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy.Hierarchical;
import stupaq.commons.base.Function2;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;

import static com.google.common.collect.FluentIterable.from;
import static stupaq.compact.CompactSerializers.Collection;

public final class ZoneHierarchy<Payload extends Hierarchical> implements Serializable {
  private static final long serialVersionUID = 1L;
  private final HashMap<LocalName, ZoneHierarchy<Payload>> childZones = Maps.newHashMap();
  private Payload payload;
  private ZoneHierarchy<Payload> parentZone;

  public ZoneHierarchy(Payload payload) {
    Preconditions.checkNotNull(payload);
    this.payload = payload;
    this.parentZone = null;
  }

  public ZoneHierarchy<Payload> parent() {
    return parentZone;
  }

  public ZoneHierarchy<Payload> parent(int levels) {
    Preconditions.checkArgument(levels >= 0);
    if (levels == 0) {
      return this;
    } else if (levels == 1) {
      // This way we avoid NPE in a trivial case where this is a root
      return parent();
    } else {
      return parent().parent(levels - 1);
    }
  }

  public boolean isLeaf() {
    return childZones.isEmpty();
  }

  public Collection<ZoneHierarchy<Payload>> children() {
    return childZones.values();
  }

  public FluentIterable<Payload> childPayloads() {
    return from(childZones.values()).transform(new Function<ZoneHierarchy<Payload>, Payload>() {
      @Override
      public Payload apply(ZoneHierarchy<Payload> zone) {
        return zone.payload;
      }
    });
  }

  public void attachTo(ZoneHierarchy<Payload> parent) {
    Preconditions.checkNotNull(parent);
    detach();
    parentZone = parent;
    parent().childZones.put(payload.localName(), this);
  }

  public void detach() {
    if (parent() != null) {
      parent().childZones.remove(payload.localName());
      parentZone = null;
    }
  }

  public Payload payload() {
    return payload;
  }

  private LocalName localName() {
    return payload.localName();
  }

  public GlobalName globalName() {
    final Builder builder = new Builder();
    synthesizePath(new InPlaceSynthesizer<Payload>() {
      @Override
      protected void process(Iterable<Payload> payloads, Payload payload) {
        builder.parent(payload.localName());
      }
    });
    return builder.build();
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
    Preconditions.checkState(parent() == null && localName().equals(LocalName.getRoot()),
        "Resolving global name from non-root");
    Iterator<LocalName> suffix = globalName.iterator();
    assert suffix.next().equals(LocalName.getRoot());
    return suffix;
  }

  public Optional<Payload> findPayload(GlobalName globalName) {
    Optional<ZoneHierarchy<Payload>> zone = find(globalName);
    return Optional.fromNullable(zone.isPresent() ? zone.get().payload() : null);
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

  public void filterLeaves(Predicate<Payload> filter) {
    for (ZoneHierarchy<Payload> zone : findLeaves().toList()) {
      if (!filter.apply(zone.payload())) {
        zone.detach();
      }
    }
  }

  private FluentIterable<ZoneHierarchy<Payload>> findLeaves() {
    return from(childZones.values()).transformAndConcat(
        new Function<ZoneHierarchy<Payload>, Iterable<? extends ZoneHierarchy<Payload>>>() {
          @Override
          public Iterable<? extends ZoneHierarchy<Payload>> apply(ZoneHierarchy<Payload> zone) {
            return zone.isLeaf() ? Collections.singletonList(zone) : zone.findLeaves();
          }
        });
  }

  public void synthesize(Synthesizer<Payload> action) {
    this.payload = action.apply(this.childPayloads(), this.payload);
  }

  public void synthesizeFromLeaves(Synthesizer<Payload> action) {
    Set<ZoneHierarchy<Payload>> added = new HashSet<>();
    Queue<ZoneHierarchy<Payload>> queue =
        findLeaves().copyInto(new ArrayDeque<ZoneHierarchy<Payload>>());
    while (!queue.isEmpty()) {
      ZoneHierarchy<Payload> current = queue.remove();
      current.synthesize(action);
      if (current != this && current.parent() != null && added.add(current.parent())) {
        queue.add(current.parent());
      }
    }
  }

  public void synthesizePath(Synthesizer<Payload> action) {
    ZoneHierarchy<Payload> current = this;
    while (current != null) {
      current.payload = action.apply(current.childPayloads(), current.payload);
      current = current.parent();
    }
  }

  public <Result extends Hierarchical> ZoneHierarchy<Result> map(Function<Payload, Result> fun) {
    ZoneHierarchy<Result> node = new ZoneHierarchy<>(fun.apply(payload()));
    for (ZoneHierarchy<Payload> child : childZones.values()) {
      child.map(fun).attachTo(node);
    }
    return node;
  }

  public Payload insert(GlobalName globalName, Inserter<Payload> inserter) {
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
    return relative.hasNext() ? child.insert(relative, inserter) : child.payload();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    appendTo(result);
    return result.toString();
  }

  private void appendTo(StringBuilder builder) {
    builder.append("\tZone: ").append(globalName()).append('\n');
    builder.append(payload.toString()).append("\n");
    for (ZoneHierarchy<Payload> child : childZones.values()) {
      child.appendTo(builder);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZoneHierarchy that = (ZoneHierarchy) o;
    return childZones.equals(that.childZones) && payload.equals(that.payload);
  }

  @Override
  public int hashCode() {
    int result = childZones.hashCode();
    result = 31 * result + payload.hashCode();
    return result;
  }

  public static <Payload extends Hierarchical> ZoneHierarchy<Payload> create(GlobalName path,
      Inserter<Payload> inserter) {
    ZoneHierarchy<Payload> root = new ZoneHierarchy<>(inserter.create(LocalName.getRoot()));
    root.insert(path, inserter);
    return root;
  }

  public static <Payload extends Hierarchical & CompactSerializable> CompactSerializer<ZoneHierarchy<Payload>> Serializer(
      final CompactSerializer<Payload> payloadSerializer) {
    return new CompactSerializer<ZoneHierarchy<Payload>>() {
      @Override
      public ZoneHierarchy<Payload> readInstance(CompactInput in) throws IOException {
        ZoneHierarchy<Payload> node = new ZoneHierarchy<>(payloadSerializer.readInstance(in));
        for (ZoneHierarchy<Payload> zone : Collection(this).readInstance(in)) {
          zone.attachTo(node);
        }
        return node;
      }

      @Override
      public void writeInstance(CompactOutput out, ZoneHierarchy<Payload> object)
          throws IOException {
        // ZoneHierarchy forms a tree, we are good to go with usual serialization schema
        payloadSerializer.writeInstance(out, object.payload);
        Collection(this).writeInstance(out, object.childZones.values());
      }
    };
  }

  public static interface Hierarchical extends stupaq.cloudatlas.naming.LocallyNameable {
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
      implements Function<Payload, Payload> {
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
    public void descend(LocalName root) {
      // Do nothing
    }

    public abstract Payload create(LocalName local);
  }
}
