package stupaq.cloudatlas.plugins.contact;

import java.util.Random;

import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection.LevelSelection;

public class UniformLevel implements LevelSelection {
  protected final Random random = new Random();

  public UniformLevel(CAConfiguration config) {
  }

  @Override
  public int select(GlobalName name) {
    return random.nextInt(name.level()) + 1;
  }
}
