package stupaq.cloudatlas.gossiping.peerstate;

import io.netty.channel.ChannelHandlerContext;
import stupaq.cloudatlas.gossiping.dataformat.GossipId;
import stupaq.cloudatlas.gossiping.dataformat.WireGossip;
import stupaq.cloudatlas.services.busybody.BusybodyConfigKeys;

public class GossipSessionIndex implements BusybodyConfigKeys {
  public void received(GossipId gossipId) {
  }

  public void sending(WireGossip msg, ChannelHandlerContext ctx) {
  }
}
