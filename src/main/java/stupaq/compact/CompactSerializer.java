package stupaq.compact;

import java.io.IOException;

public interface CompactSerializer<Type> {
  public Type readInstance(CompactInput in) throws IOException;

  public void writeInstance(CompactOutput out, Type object) throws IOException;
}
