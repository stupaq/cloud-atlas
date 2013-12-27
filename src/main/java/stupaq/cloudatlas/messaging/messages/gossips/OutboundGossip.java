package stupaq.cloudatlas.messaging.messages.gossips;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.messages.Message;

@Immutable
public final class OutboundGossip extends Gossip {
  public OutboundGossip(CAContact contact, Message gossip) {
    super(contact, gossip);
  }
}
