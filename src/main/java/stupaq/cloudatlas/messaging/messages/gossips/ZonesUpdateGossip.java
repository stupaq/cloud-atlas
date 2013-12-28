package stupaq.cloudatlas.messaging.messages.gossips;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

import static stupaq.compact.CompactSerializers.Map;

@Immutable
public class ZonesUpdateGossip extends Gossip
    implements Iterable<Entry<GlobalName, ZoneManagementInfo>> {
  public static final CompactSerializer<ZonesUpdateGossip> SERIALIZER =
      new CompactSerializer<ZonesUpdateGossip>() {
        @Override
        public ZonesUpdateGossip readInstance(CompactInput in) throws IOException {
          return new ZonesUpdateGossip(
              Map(GlobalName.SERIALIZER, ZoneManagementInfo.SERIALIZER).readInstance(in));
        }

        @Override
        public void writeInstance(CompactOutput out, ZonesUpdateGossip object) throws IOException {
          Map(GlobalName.SERIALIZER, ZoneManagementInfo.SERIALIZER).writeInstance(out,
              object.zones);
        }
      };
  private final Map<GlobalName, ZoneManagementInfo> zones;

  public ZonesUpdateGossip(Map<GlobalName, ZoneManagementInfo> zones) {
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
