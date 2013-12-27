package stupaq.compact;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompactInput extends DataInputStream {
  public CompactInput(InputStream input) throws IOException {
    super(input);
  }
}
