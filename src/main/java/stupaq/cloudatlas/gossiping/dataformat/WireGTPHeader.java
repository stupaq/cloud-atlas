package stupaq.cloudatlas.gossiping.dataformat;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;

public class WireGTPHeader extends ForwardingList<Long> {
  public static final int SERIALIZED_MAX_SIZE = 8 * 4 + 1;
  public static final CompactSerializer<WireGTPHeader> SERIALIZER =
      new CompactSerializer<WireGTPHeader>() {
        @Override
        public WireGTPHeader readInstance(CompactInput in) throws IOException {
          WireGTPHeader header = new WireGTPHeader();
          for (int samples = in.readByte(); samples > 0; samples--) {
            header.add(in.readLong());
          }
          return header;
        }

        @Override
        public void writeInstance(CompactOutput out, WireGTPHeader object) throws IOException {
          out.writeByte(object.size());
          for (Long value : object) {
            out.writeLong(value);
          }
        }
      };
  private final List<Long> samples = new ArrayList<>(4);

  @Override
  protected List<Long> delegate() {
    return null;
  }

  @Override
  public void add(int index, Long element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(int index, Collection<? extends Long> elements) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(Long element) {
    Preconditions.checkArgument(size() < 4);
    Preconditions.checkNotNull(element);
    return super.add(element);
  }
}
