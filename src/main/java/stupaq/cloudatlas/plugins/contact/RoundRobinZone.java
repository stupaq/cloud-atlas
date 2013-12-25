package stupaq.cloudatlas.plugins.contact;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import java.util.Map;

import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.busybody.strategies.ZoneSelection;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;

@SuppressWarnings("unused")
public class RoundRobinZone implements ZoneSelection {
  Map<GlobalName, Integer> nextIndices = Maps.newHashMap();

  public RoundRobinZone(CAConfiguration config) {
  }

  @Override
  public ZoneManagementInfo select(GlobalName parent, FluentIterable<ZoneManagementInfo> zones) {
    Integer index = nextIndices.get(parent);
    nextIndices.put(parent, (index == null ? 1 : index + 1) % zones.size());
    return zones.get(index == null ? 0 : index);
  }
}
