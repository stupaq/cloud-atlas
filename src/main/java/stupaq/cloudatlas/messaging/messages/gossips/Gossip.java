package stupaq.cloudatlas.messaging.messages.gossips;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.messages.Message;

@Immutable
public abstract class Gossip extends Message {
  private final CAContact contact;
  // FIXME make it serializable
  private final Message gossip;

  protected Gossip(CAContact contact, Message gossip) {
    Preconditions.checkArgument(!(gossip instanceof Gossip));
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
