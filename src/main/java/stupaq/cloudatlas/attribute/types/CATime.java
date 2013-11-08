package stupaq.cloudatlas.attribute.types;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CATime extends LongStub implements Value {
  @SerializationOnly
  public CATime() {
    this(0L);
  }

  public CATime(Long value) {
    super(value);
  }
}
