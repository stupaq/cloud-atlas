package stupaq.cloudatlas.services.busybody.strategies;

import com.google.common.collect.FluentIterable;

import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.zonemanager.ZoneManagementInfo;

public interface ZoneSelection {
  public ZoneManagementInfo select(GlobalName parent, FluentIterable<ZoneManagementInfo> zones);
}
