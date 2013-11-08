package stupaq.cloudatlas.attribute.types;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.serialization.SerializationOnly;

public class CADuration extends LongStub implements Value {
  @SerializationOnly
  public CADuration() {
    this(0L);
  }

  public CADuration(Long value) {
    super(value);
  }
}
