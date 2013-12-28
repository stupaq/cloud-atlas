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
 */
package stupaq.cloudatlas.gossiping;
