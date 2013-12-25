package stupaq.cloudatlas.plugins.contact;

import java.util.Random;

import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection.LevelSelection;

public class RandomLevel implements LevelSelection {
  private final Random random = new Random();

  public RandomLevel(CAConfiguration config) {
  }

  @Override
  public int select(GlobalName name) {
    return random.nextInt(name.leafLevel()) + 1;
  }
}
