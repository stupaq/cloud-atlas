package stupaq.cloudatlas.messaging.messages.gossips;

import com.google.common.base.Optional;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.time.GTPOffset;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

import static stupaq.compact.CompactSerializers.Map;

@Immutable
public abstract class AbstractZonesInterestGossip extends Gossip
    implements Iterable<Entry<GlobalName, CATime>> {
  protected final GlobalName leaf;
  protected final Map<GlobalName, CATime> timestamps;

  protected AbstractZonesInterestGossip(GlobalName leaf, Map<GlobalName, CATime> timestamps) {
    this.leaf = leaf;
    this.timestamps = timestamps;
  }

  @Override
  public final Iterator<Entry<GlobalName, CATime>> iterator() {
    return timestamps.entrySet().iterator();
  }

  public final CATime getTimestamp(GlobalName name) {
    return Optional.fromNullable(timestamps.get(name)).or(new CATime());
  }

  public final GlobalName getLeaf() {
    return leaf;
  }

  @Override
  public final void adjustToLocal(GTPOffset offset) {
    for (Entry<GlobalName, CATime> entry : timestamps.entrySet()) {
      entry.setValue(offset.toLocal(entry.getValue()));
    }
  }

  @Override
  public String toString() {
    return "{sender = " + sender() + ", gossipId = " + id() + ", leaf = " + leaf + ", timestamps=" +
        timestamps + '}';
  }

  protected static abstract class AbstractCompactSerializer<Type extends AbstractZonesInterestGossip>
      implements CompactSerializer<Type> {
    @Override
    public final void writeInstance(CompactOutput out, Type object) throws IOException {
      GlobalName.SERIALIZER.writeInstance(out, object.getLeaf());
      Map(GlobalName.SERIALIZER, CATime.SERIALIZER).writeInstance(out, object.timestamps);
    }
  }
}
