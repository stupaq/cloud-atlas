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
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

import static stupaq.compact.CompactSerializers.Map;

@Immutable
public class ZonesInterestGossip extends Gossip implements Iterable<Entry<GlobalName, CATime>> {
  public static final CompactSerializer<ZonesInterestGossip> SERIALIZER =
      new CompactSerializer<ZonesInterestGossip>() {
        @Override
        public ZonesInterestGossip readInstance(CompactInput in) throws IOException {
          return new ZonesInterestGossip(GlobalName.SERIALIZER.readInstance(in),
              Map(GlobalName.SERIALIZER, CATime.SERIALIZER).readInstance(in));
        }

        @Override
        public void writeInstance(CompactOutput out, ZonesInterestGossip object)
            throws IOException {
          GlobalName.SERIALIZER.writeInstance(out, object.getLeaf());
          Map(GlobalName.SERIALIZER, CATime.SERIALIZER).writeInstance(out, object.timestamps);
        }
      };
  protected final GlobalName leaf;
  protected final Map<GlobalName, CATime> timestamps;

  public ZonesInterestGossip(GlobalName leaf, Map<GlobalName, CATime> timestamps) {
    this.leaf = leaf;
    this.timestamps = timestamps;
  }

  @Override
  public Iterator<Entry<GlobalName, CATime>> iterator() {
    return timestamps.entrySet().iterator();
  }

  public CATime getTimestamp(GlobalName name) {
    return Optional.fromNullable(timestamps.get(name)).or(new CATime());
  }

  public GlobalName getLeaf() {
    return leaf;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZonesInterestGossip;
  }

  @Override
  public String toString() {
    return "ZonesInterestGossip{sender=" + sender() + ", leaf=" + leaf + ", timestamps=" +
        timestamps + '}';
  }

  @Override
  public void adjustToLocal(GTPOffset offset) {
    for (Entry<GlobalName, CATime> entry : timestamps.entrySet()) {
      entry.setValue(offset.toLocal(entry.getValue()));
    }
  }
}
