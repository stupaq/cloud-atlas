/**
 * Convention on working with {@link io.netty.util.ReferenceCounted} objects goes as follows:
 * <p>
 *   If a reference is passed to a function (or constructor), the function is responsible for
 *   calling {@link io.netty.util.ReferenceCounted#retain()} if the reference goes beyond
 *   function's scope.
 * </p>
 * <p>
 *   If a function returns a reference it must call {@link io.netty.util.ReferenceCounted#retain()}
 *   since the reference goes beyond function's scope.
 * </p>
 *
 * Protocol quirks:
 * <p>
 *   Protocol is based on the assumption that node cannot send two outbound gossip within the same
 *   session to the same node without a response from the node between these messages.
 *   IT WILL NOT WORK WITHOUT THIS ASSUMPTION!
 * </p>
 * <p>
 *   We can potentially have two concurrent sessions between the same nodes utilizing the same
 *   session id. Each of them initiated by different peer.
 *   The protocol is perfectly fine with described situation since each looking from the perspective
 *   of the node that initiated the session, each inbound message carries a different odd
 *   {@link stupaq.cloudatlas.gossiping.dataformat.GossipId} and each outbound message carries even
 *   id. That means conflicting sessions, as seen bya single node will have interleaving ids.
 * </p>
 */
package stupaq.cloudatlas.gossiping;
