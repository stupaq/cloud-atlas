package stupaq.cloudatlas.messaging.messages.gossips;

import stupaq.cloudatlas.messaging.messages.Message;

public class InboundGossip extends Message {
  private final Gossip gossip;

  public InboundGossip(Gossip gossip) {
    this.gossip = gossip;
  }

  public Gossip gossip() {
    return gossip;
  }

  @Override
  public String toString() {
    return "InboundGossip{gossip=" + gossip + '}';
  }
}
