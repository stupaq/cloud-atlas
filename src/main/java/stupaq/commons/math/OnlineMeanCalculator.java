package stupaq.commons.math;

public class OnlineMeanCalculator {
  private int count = 0;
  private double mean = 0;
  private double accumulator = 0;

  public void add(double sample) {
    if (count == Integer.MAX_VALUE) {
      reset();
    }
    count++;
    double delta = sample - mean;
    mean += delta / count;
    accumulator += delta * (sample - mean);
  }

  public void reset() {
    count = 0;
    mean = 0;
    accumulator = 0;
  }

  public double mean() {
    return mean;
  }

  public double variance() {
    return count > 1 ? accumulator / (count - 1) : Double.POSITIVE_INFINITY;
  }

  public double sigma() {
    return Math.sqrt(variance());
  }

  @Override
  public String toString() {
    return "OnlineMeanCalculator{count=" + count + ", mean=" + mean + ", sigma=" + sigma() + '}';
  }
}
