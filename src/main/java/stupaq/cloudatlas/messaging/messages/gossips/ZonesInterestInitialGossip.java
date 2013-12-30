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
public class ZonesInterestInitialGossip extends ZonesInterestResponseGossip {
  public static final CompactSerializer<ZonesInterestInitialGossip> SERIALIZER =
      new AbstractCompactSerializer<ZonesInterestInitialGossip>() {
        @Override
        public ZonesInterestInitialGossip readInstance(CompactInput in) throws IOException {
          return new ZonesInterestInitialGossip(GlobalName.SERIALIZER.readInstance(in),
              Map(GlobalName.SERIALIZER, CATime.SERIALIZER).readInstance(in));
        }
      };

  public ZonesInterestInitialGossip(GlobalName leaf, Map<GlobalName, CATime> timestamps) {
    super(leaf, timestamps);
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZonesInterestInitialGossip;
  }

  @Override
  public String toString() {
    return "ZonesInterestInitial" + super.toString();
  }
}
