package stupaq.cloudatlas.serialization;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public interface CompactSerializable {
  public void readFields(ObjectInput in) throws IOException, ClassNotFoundException;

  public void writeFields(ObjectOutput out) throws IOException;
}
