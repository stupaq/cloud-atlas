package stupaq.cloudatlas.gossiping.peerstate;

import stupaq.cloudatlas.gossiping.dataformat.GossipId;

public class GossipIdAllocator {
  private GossipId nextId = new GossipId();

  public GossipId next() {
    try {
      return nextId;
    } finally {
      nextId = nextId.nextGossip();
    }
  }
}
