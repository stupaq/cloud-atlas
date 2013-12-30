package stupaq.cloudatlas.messaging.messages.gossips;

import java.io.IOException;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

import static stupaq.compact.CompactSerializers.Map;

@Immutable
public class ZonesInterestResponseGossip extends AbstractZonesInterestGossip {
  public static final CompactSerializer<ZonesInterestResponseGossip> SERIALIZER =
      new AbstractCompactSerializer<ZonesInterestResponseGossip>() {
        @Override
        public ZonesInterestResponseGossip readInstance(CompactInput in) throws IOException {
          return new ZonesInterestResponseGossip(GlobalName.SERIALIZER.readInstance(in),
              Map(GlobalName.SERIALIZER, CATime.SERIALIZER).readInstance(in));
        }
      };

  public ZonesInterestResponseGossip(GlobalName leaf, Map<GlobalName, CATime> timestamps) {
    super(leaf, timestamps);
  }

  @Override
  public Gossip respondsTo(Gossip gossip) {
    return super.respondsTo(gossip);
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZonesInterestGossip;
  }

  @Override
  public String toString() {
    return "ZonesInterestResponseGossip" + super.toString();
  }
}
