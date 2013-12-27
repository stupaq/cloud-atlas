package stupaq.cloudatlas.messaging.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.messages.gossips.Gossip;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

import static stupaq.compact.CompactSerializers.Map;

@Immutable
public class ZonesUpdateMessage extends Gossip
    implements Iterable<Entry<GlobalName, ZoneManagementInfo>> {
  public static final CompactSerializer<ZonesUpdateMessage> SERIALIZER =
      new CompactSerializer<ZonesUpdateMessage>() {
        @Override
        public ZonesUpdateMessage readInstance(ObjectInput in) throws IOException {
          return new ZonesUpdateMessage(
              Map(GlobalName.SERIALIZER, ZoneManagementInfo.SERIALIZER).readInstance(in));
        }

        @Override
        public void writeInstance(ObjectOutput out, ZonesUpdateMessage object) throws IOException {
          Map(GlobalName.SERIALIZER, ZoneManagementInfo.SERIALIZER).writeInstance(out,
              object.zones);
        }
      };
  private final Map<GlobalName, ZoneManagementInfo> zones;

  public ZonesUpdateMessage(Map<GlobalName, ZoneManagementInfo> zones) {
    this.zones = zones;
  }

  @Override
  public Iterator<Entry<GlobalName, ZoneManagementInfo>> iterator() {
    return zones.entrySet().iterator();
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZonesUpdateMessage;
  }
}
