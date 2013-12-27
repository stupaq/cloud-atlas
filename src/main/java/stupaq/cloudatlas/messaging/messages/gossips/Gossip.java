package stupaq.cloudatlas.messaging.messages.gossips;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.messages.Message;

@Immutable
public abstract class Gossip extends Message {
  private final CAContact contact;
  private final Message gossip;

  public Gossip(CAContact contact, Message gossip) {
    this.contact = contact;
    this.gossip = gossip;
  }

  public CAContact getContact() {
    return contact;
  }

  public Message getGossip() {
    return gossip;
  }
}
