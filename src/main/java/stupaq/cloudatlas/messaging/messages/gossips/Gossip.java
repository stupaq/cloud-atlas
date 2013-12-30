package stupaq.cloudatlas.messaging.messages.gossips;

import com.google.common.base.Preconditions;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.gossiping.dataformat.GossipId;
import stupaq.cloudatlas.messaging.messages.Message;
import stupaq.cloudatlas.services.busybody.sessions.SessionId;
import stupaq.cloudatlas.time.GTPAdjustable;
import stupaq.compact.CompactSerializable;

public abstract class Gossip extends Message implements CompactSerializable, GTPAdjustable {
  /** This field should not be serialized, instead we set it from datagram. */
  private transient CAContact contact = null;
  /** This field should not be serialized, instead we encode it into datagram. */
  private transient GossipId gossipId = null;

  public final CAContact sender() {
    Preconditions.checkNotNull(contact);
    return contact;
  }

  public final Gossip sender(CAContact contact) {
    Preconditions.checkNotNull(contact);
    this.contact = contact;
    return this;
  }

  public final boolean hasSender() {
    return contact != null;
  }

  public final GossipId id() {
    Preconditions.checkNotNull(gossipId);
    return gossipId;
  }

  protected Gossip initiates(SessionId session) {
    Preconditions.checkNotNull(session);
    this.gossipId = new GossipId(session);
    return this;
  }

  protected Gossip respondsTo(Gossip gossip) {
    Preconditions.checkNotNull(gossip.gossipId);
    gossipId = gossip.gossipId.nextGossip();
    return this;
  }
}
