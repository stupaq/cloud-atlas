package stupaq.compact;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public interface CompactSerializer<Type> {
  public Type readInstance(ObjectInput in) throws IOException;

  public void writeInstance(ObjectOutput out, Type object) throws IOException;
}
