package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.base.Preconditions;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import stupaq.cloudatlas.gossiping.peerstate.GTPSample;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

import static io.netty.buffer.Unpooled.copyBoolean;
import static io.netty.buffer.Unpooled.unmodifiableBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;

public class WireGTPHeader {
  public static final int SERIALIZED_MIN_SIZE = 1;
  public static final int SERIALIZED_MAX_SIZE = 8 * 4 + 1;
  public static final ByteBuf SERIALIZED_EMPTY =
      unreleasableBuffer(unmodifiableBuffer(copyBoolean(false)));
  public static final CompactSerializer<WireGTPHeader> SERIALIZER =
      new CompactSerializer<WireGTPHeader>() {
        @Override
        public WireGTPHeader readInstance(CompactInput in) throws IOException {
          WireGTPHeader header = new WireGTPHeader();
          for (int samples = in.readByte(); samples > 0; samples--) {
            header.record(in.readLong());
          }
          return header;
        }

        @Override
        public void writeInstance(CompactOutput out, WireGTPHeader object) throws IOException {
          out.writeByte(object.samplesCount);
          for (long value : object.samples) {
            out.writeLong(value);
          }
        }
      };
  public static final int READY = 4;
  public static final int HALF = 2;
  public static final int EMPTY = 0;
  private final long[] samples = new long[4];
  private int samplesCount = 0;

  public void record(long timestamp) {
    Preconditions.checkState(!is(READY));
    samples[samplesCount++] = timestamp;
  }

  public GTPSample extractAndFlip() {
    Preconditions.checkState(is(READY));
    try {
      return new GTPSample(samples);
    } finally {
      samples[0] = samples[2];
      samples[1] = samples[3];
      samplesCount = 2;
      assert is(WireGTPHeader.HALF);
    }
  }

  public boolean is(int state) {
    return samplesCount == state;
  }
}
