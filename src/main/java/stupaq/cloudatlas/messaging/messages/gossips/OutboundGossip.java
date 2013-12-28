package stupaq.cloudatlas.messaging.messages.gossips;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.messages.Message;

@Immutable
public final class OutboundGossip extends Message {
  private final CAContact recipient;
  private final Gossip gossip;

  public OutboundGossip(CAContact recipient, Gossip gossip) {
    this.recipient = recipient;
    this.gossip = gossip;
  }

  public Gossip gossip() {
    return gossip;
  }

  public CAContact recipient() {
    return recipient;
  }

  @Override
  public String toString() {
    return "OutboundGossip{recipient=" + recipient + ", gossip=" + gossip + '}';
  }
}
