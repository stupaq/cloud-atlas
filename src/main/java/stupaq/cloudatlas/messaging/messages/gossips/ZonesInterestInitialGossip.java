package stupaq.cloudatlas.messaging.messages.gossips;

import java.io.IOException;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

@Immutable
public class ZonesInterestInitialGossip extends ZonesInterestGossip {
  public static final CompactSerializer<ZonesInterestInitialGossip> SERIALIZER =
      new CompactSerializer<ZonesInterestInitialGossip>() {
        @Override
        public ZonesInterestInitialGossip readInstance(CompactInput in) throws IOException {
          return new ZonesInterestInitialGossip(ZonesInterestGossip.SERIALIZER.readInstance(in));
        }

        @Override
        public void writeInstance(CompactOutput out, ZonesInterestInitialGossip object)
            throws IOException {
          ZonesInterestGossip.SERIALIZER.writeInstance(out, object);
        }
      };

  public ZonesInterestInitialGossip(GlobalName leaf, Map<GlobalName, CATime> timestamps) {
    super(leaf, timestamps);
  }

  public ZonesInterestInitialGossip(ZonesInterestGossip gossip) {
    super(gossip.leaf, gossip.timestamps);
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZonesInterestInitialGossip;
  }

  @Override
  public String toString() {
    return "ZonesInterestInitialGossip{sender=" + sender() + ", leaf=" + leaf + ", timestamps=" +
        timestamps + '}';
  }
}
