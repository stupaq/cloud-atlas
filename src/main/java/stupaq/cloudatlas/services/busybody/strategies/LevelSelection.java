package stupaq.cloudatlas.services.busybody.strategies;

import stupaq.cloudatlas.naming.GlobalName;

public interface LevelSelection {
  public int select(GlobalName name);
}
