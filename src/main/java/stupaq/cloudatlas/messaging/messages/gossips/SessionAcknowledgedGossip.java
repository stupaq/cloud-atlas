package stupaq.cloudatlas.messaging.messages.gossips;

import java.io.IOException;

import stupaq.cloudatlas.time.GTPOffset;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

public class SessionAcknowledgedGossip extends Gossip {
  public static final CompactSerializer<SessionAcknowledgedGossip> SERIALIZER =
      new CompactSerializer<SessionAcknowledgedGossip>() {
        @Override
        public SessionAcknowledgedGossip readInstance(CompactInput in) throws IOException {
          return new SessionAcknowledgedGossip();
        }

        @Override
        public void writeInstance(CompactOutput out, SessionAcknowledgedGossip object)
            throws IOException {
          // Nothing to do.
        }
      };

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.SessionAcknowledgedGossip;
  }

  @Override
  public void adjustToLocal(GTPOffset offset) {
    // Nothing to do.
  }

  @Override
  public String toString() {
    return "SessionAcknowledged" + super.toString();
  }

  @Override
  public Gossip respondsTo(Gossip gossip) {
    return super.respondsTo(gossip);
  }
}
