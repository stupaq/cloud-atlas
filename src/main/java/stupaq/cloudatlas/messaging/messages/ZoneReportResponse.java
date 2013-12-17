package stupaq.cloudatlas.messaging.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.List;

import stupaq.cloudatlas.messaging.Message;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

public class ZoneReportResponse extends Message implements Iterable<GlobalName> {
  public static final CompactSerializer<ZoneReportResponse> SERIALIZER =
      new CompactSerializer<ZoneReportResponse>() {
        @Override
        public ZoneReportResponse readInstance(ObjectInput in) throws IOException {
          return new ZoneReportResponse(CompactSerializers.List(GlobalName.SERIALIZER)
              .readInstance(in));
        }

        @Override
        public void writeInstance(ObjectOutput out, ZoneReportResponse object) throws IOException {
          CompactSerializers.List(GlobalName.SERIALIZER).writeInstance(out, object.zones);
        }
      };
  private final List<GlobalName> zones;

  public ZoneReportResponse(List<GlobalName> zones) {
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
