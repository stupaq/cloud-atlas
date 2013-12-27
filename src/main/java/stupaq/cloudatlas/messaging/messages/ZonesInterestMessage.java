package stupaq.cloudatlas.messaging.messages;

import com.google.common.base.Optional;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.messaging.messages.gossips.Gossip;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

import static stupaq.compact.CompactSerializers.Map;

@Immutable
public class ZonesInterestMessage extends Gossip implements Iterable<Entry<GlobalName, CATime>> {
  public static final CompactSerializer<ZonesInterestMessage> SERIALIZER =
      new CompactSerializer<ZonesInterestMessage>() {
        @Override
        public ZonesInterestMessage readInstance(ObjectInput in) throws IOException {
          return new ZonesInterestMessage(GlobalName.SERIALIZER.readInstance(in),
              Map(GlobalName.SERIALIZER, CATime.SERIALIZER).readInstance(in));
        }

        @Override
        public void writeInstance(ObjectOutput out, ZonesInterestMessage object)
            throws IOException {
          GlobalName.SERIALIZER.writeInstance(out, object.getLeaf());
          Map(GlobalName.SERIALIZER, CATime.SERIALIZER).writeInstance(out, object.timestamps);
        }
      };
  private final GlobalName leaf;
  private final Map<GlobalName, CATime> timestamps;

  public ZonesInterestMessage(GlobalName leaf, Map<GlobalName, CATime> timestamps) {
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
    return TypeDescriptor.ZonesInterestMessage;
  }
}
