package stupaq.cloudatlas.messaging.messages.gossips;

import com.google.common.base.Preconditions;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.messages.Message;
import stupaq.cloudatlas.time.GTPAdjustable;
import stupaq.compact.CompactSerializable;

public abstract class Gossip extends Message implements CompactSerializable, GTPAdjustable {
  /** This field should not be serialized, instead we set it from datagram. */
  private transient CAContact contact = null;

  public final CAContact sender() {
    Preconditions.checkNotNull(contact);
    return contact;
  }

  public final Gossip sender(CAContact contact) {
    this.contact = contact;
    return this;
  }

  public final boolean hasSender() {
    return contact != null;
  }
}
