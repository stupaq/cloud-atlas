package stupaq.cloudatlas.attribute.types;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CAInteger extends LongStub implements Value {
  @SerializationOnly
  public CAInteger() {
    this(0L);
  }

  public CAInteger(Long value) {
    super(value);
  }
}
