package stupaq.cloudatlas.plugins.contact;

import java.util.Random;

import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.busybody.strategies.LevelSelection;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;

@SuppressWarnings("unused")
public class ExponentialLevel implements LevelSelection {
  public static final String MEAN = "exponential_level_mean";
  public static final double MEAN_DEFAULT = 0.5;
  private final Random random = new Random();
  private final double mean;

  public ExponentialLevel(CAConfiguration config) {
    mean = config.getDouble(MEAN, MEAN_DEFAULT);
  }

  @Override
  public int select(GlobalName name) {
    double num = -mean * log(1 - random.nextDouble());
    return (int) (min(name.leafLevel() - 1, max(0, num)) + 1);
  }
}
