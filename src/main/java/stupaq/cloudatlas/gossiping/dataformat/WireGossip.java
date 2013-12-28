package stupaq.cloudatlas.gossiping.dataformat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.gossiping.peerstate.GossipInfo;

public class WireGossip {
  private final CAContact contact;
  private final GossipId gossipId;
  private final ByteBuf data;

  public WireGossip(CAContact contact, GossipId gossipId, ByteBuf data) {
    this.contact = contact;
    this.gossipId = gossipId;
    // We save the reference
    this.data = data.retain();
  }

  public WireGossip(CAContact contact, GossipId gossipId, GossipInfo gossip) {
    this.contact = contact;
    this.gossipId = gossipId;
    // We pass over reference created by assemble() to WireGossip's field
    data = gossip.assemble();
  }

  public CAContact contact() {
    return contact;
  }

  public GossipId gossipId() {
    return gossipId;
  }

  public ByteBuf data() {
    return data.retain();
  }

  /** The returned reference is valid for the lifetime of {@code this} only. */
  public ByteBufInputStream dataStream() {
    return new ByteBufInputStream(data);
  }

  @Override
  public String toString() {
    return "WireGossip{" + "contact=" + contact + ", gossipId=" + gossipId + ", data=" + data + '}';
  }
}
