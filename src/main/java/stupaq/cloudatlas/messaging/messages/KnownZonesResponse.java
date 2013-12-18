package stupaq.cloudatlas.messaging.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.List;

import stupaq.cloudatlas.messaging.Response;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

public class KnownZonesResponse extends Response<KnownZonesRequest>
    implements CompactSerializable, Iterable<GlobalName> {
  public static final CompactSerializer<KnownZonesResponse> SERIALIZER =
      new CompactSerializer<KnownZonesResponse>() {
        @Override
        public KnownZonesResponse readInstance(ObjectInput in) throws IOException {
          return new KnownZonesResponse(
              CompactSerializers.List(GlobalName.SERIALIZER).readInstance(in));
        }

        @Override
        public void writeInstance(ObjectOutput out, KnownZonesResponse object) throws IOException {
          CompactSerializers.List(GlobalName.SERIALIZER).writeInstance(out, object.zones);
        }
      };
  private final List<GlobalName> zones;

  public KnownZonesResponse(List<GlobalName> zones) {
    this.zones = zones;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZoneReportResponse;
  }

  @Override
  public Iterator<GlobalName> iterator() {
    return zones.iterator();
  }
}
