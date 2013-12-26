package stupaq.cloudatlas.messaging.messages.gossips;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.Message;
import stupaq.cloudatlas.messaging.messages.Gossip;

public final class InboundGossip extends Gossip {
  public InboundGossip(CAContact contact, Message gossip) {
    super(contact, gossip);
  }
}
