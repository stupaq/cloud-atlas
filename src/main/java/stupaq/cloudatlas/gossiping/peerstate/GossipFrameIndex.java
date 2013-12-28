package stupaq.cloudatlas.gossiping.peerstate;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.gossiping.dataformat.FrameId;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;

public class GossipFrameIndex extends AbstractReferenceCounted {
  private ByteBuf[] received = null;
  private int missingFrames;

  public boolean add(WireFrame frame) throws Exception {
    FrameId frameId = frame.frameId();
    int claimedFramesCount = frameId.framesCount();
    if (received == null) {
      missingFrames = claimedFramesCount;
      received = new ByteBuf[missingFrames];
    }
    Preconditions.checkArgument(claimedFramesCount == received.length);
    int seqNo = frameId.sequenceNumber();
    Preconditions.checkArgument(seqNo < received.length);
    if (received[seqNo] == null) {
      // At this point we keep the reference
      received[seqNo] = frame.data();
      missingFrames--;
    }
    return missingFrames == 0;
  }

  public ByteBuf assemble() {
    Preconditions.checkState(missingFrames == 0);
    Preconditions.checkState(received != null);
    // We pass our reference to the caller
    ByteBuf composite = Unpooled.wrappedBuffer(received);
    received = null;
    return composite;
  }

  @Override
  protected void deallocate() {
    if (received != null) {
      for (ByteBuf frame : received) {
        ReferenceCountUtil.release(frame);
      }
    }
  }
}
