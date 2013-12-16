package stupaq.cloudatlas.bus.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.List;

import stupaq.cloudatlas.naming.GlobalName;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

public class ZoneReportMessage extends Message implements Iterable<GlobalName> {
  public static final CompactSerializer<ZoneReportMessage> SERIALIZER =
      new CompactSerializer<ZoneReportMessage>() {
        @Override
        public ZoneReportMessage readInstance(ObjectInput in) throws IOException {
          return new ZoneReportMessage(CompactSerializers.List(GlobalName.SERIALIZER)
              .readInstance(in));
        }

        @Override
        public void writeInstance(ObjectOutput out, ZoneReportMessage object) throws IOException {
          CompactSerializers.List(GlobalName.SERIALIZER).writeInstance(out, object.zones);
        }
      };
  private final List<GlobalName> zones;

  public ZoneReportMessage(List<GlobalName> zones) {
    this.zones = zones;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZoneReportMessage;
  }

  @Override
  public Iterator<GlobalName> iterator() {
    return zones.iterator();
  }
}
