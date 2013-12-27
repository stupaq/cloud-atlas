package stupaq.cloudatlas.gossiping.dataformat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.gossiping.sessions.GossipInfo;

public class EncodedGossip {
  private final CAContact contact;
  private final ByteBuf data;

  public EncodedGossip(CAContact contact, ByteBuf data) {
    this.contact = contact;
    this.data = data.retain();
  }

  public EncodedGossip(CAContact contact, GossipInfo gossip) {
    this.contact = contact;
    // We pass over reference created by assemble() to EncodedGossip's field
    data = gossip.assemble();
  }

  public CAContact contact() {
    return contact;
  }

  public ByteBuf data() {
    return data.retain();
  }

  /** The returned reference is valid for the lifetime of {@link this} only. */
  public ByteBufInputStream dataStream() {
    return new ByteBufInputStream(data);
  }
}
