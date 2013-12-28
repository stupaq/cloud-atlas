package stupaq.cloudatlas.gossiping.peerstate;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.gossiping.dataformat.FrameId;
import stupaq.cloudatlas.gossiping.dataformat.WireFrame;

public class GossipInfo extends AbstractReferenceCounted {
  private WireFrame[] received = null;
  private int missingFrames;

  public boolean add(WireFrame frame) throws Exception {
    FrameId frameId = frame.frameId();
    int claimedFramesCount = frameId.framesCount();
    if (received == null) {
      missingFrames = claimedFramesCount;
      received = new WireFrame[missingFrames];
    }
    Preconditions.checkArgument(claimedFramesCount == received.length);
    int seqNo = frameId.sequenceNumber();
    Preconditions.checkArgument(seqNo < received.length);
    if (received[seqNo] == null) {
      // At this point we keep the reference
      received[seqNo] = frame.retain();
      missingFrames--;
    }
    return missingFrames == 0;
  }

  public ByteBuf assemble() {
    Preconditions.checkState(missingFrames == 0);
    CompositeByteBuf composite = Unpooled.compositeBuffer(received.length);
    for (WireFrame frame : received) {
      // Created reference is passed to the composite
      composite.addComponent(frame.data());
    }
    return composite;
  }

  @Override
  protected void deallocate() {
    for (WireFrame frame : received) {
      ReferenceCountUtil.release(frame);
    }
  }
}
