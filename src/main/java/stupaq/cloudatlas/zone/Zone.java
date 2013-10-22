package stupaq.cloudatlas.zone;

import java.util.ArrayList;
import java.util.List;

public final class Zone {
  private Zone parentZone;
  private List<Zone> childZones;
  private ZoneManagementInfo zmi;

  public Zone(Zone parentZone) {
    this.parentZone = parentZone;
    childZones = new ArrayList<>();
  }

  public ZoneManagementInfo getZMI() {
    return zmi;
  }

  public void addZone(Zone zone) {
    childZones.add(zone);
  }
}
