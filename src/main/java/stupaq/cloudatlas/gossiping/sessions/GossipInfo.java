package stupaq.cloudatlas.gossiping.sessions;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import stupaq.cloudatlas.gossiping.dataformat.Frame;
import stupaq.cloudatlas.gossiping.dataformat.FrameId;

public class GossipInfo extends AbstractReferenceCounted {
  private Frame[] received = null;
  private int missingFrames;

  public boolean add(Frame frame) throws Exception {
    FrameId frameId = frame.frameId();
    int claimedFramesCount = frameId.gossipId().framesCount();
    if (received == null) {
      missingFrames = claimedFramesCount;
      received = new Frame[missingFrames];
    }
    Preconditions.checkArgument(claimedFramesCount == received.length);
    int seqNo = frameId.seqNo();
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
    for (Frame frame : received) {
      // Created reference is passed to composite
      composite.addComponent(frame.data());
    }
    return composite;
  }

  @Override
  protected void deallocate() {
    for (Frame frame : received) {
      ReferenceCountUtil.release(frame);
    }
  }
}
