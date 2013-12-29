package stupaq.cloudatlas.time;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CADuration;
import stupaq.cloudatlas.attribute.values.CATime;

@Immutable
public class GTPOffset {
  private final long difference;

  public GTPOffset(long difference) {
    this.difference = difference;
  }

  public long toLocal(long timestamp) {
    return timestamp - difference;
  }

  public CATime toLocal(CATime timestamp) {
    return timestamp.op().add(new CADuration(-difference)).to().Time();
  }

  @Override
  public String toString() {
    return "GTPOffset{difference=" + difference + '}';
  }
}
