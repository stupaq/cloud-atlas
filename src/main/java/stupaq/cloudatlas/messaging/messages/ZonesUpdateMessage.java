package stupaq.cloudatlas.messaging.messages;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;

public class ZonesUpdateMessage extends Message
    implements Iterable<Entry<GlobalName, ZoneManagementInfo>> {
  private final Map<GlobalName, ZoneManagementInfo> zones;

  public ZonesUpdateMessage(Map<GlobalName, ZoneManagementInfo> zones) {
    this.zones = zones;
  }

  @Override
  public Iterator<Entry<GlobalName, ZoneManagementInfo>> iterator() {
    return zones.entrySet().iterator();
  }
}
