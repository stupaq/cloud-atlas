package stupaq.compact;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CompactOutput extends DataOutputStream {
  public CompactOutput(OutputStream output) throws IOException {
    super(output);
  }
}
