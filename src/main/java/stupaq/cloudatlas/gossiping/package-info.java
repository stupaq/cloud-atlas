/**
 * Convention on working with {@link io.netty.util.ReferenceCounted} objects goes as follows:
 * if an object is passed to a function (or constructor), the function is responsible for calling
 * {@link io.netty.util.ReferenceCounted#retain()} if it saves a reference somewhere;
 * functions returning a reference must call {@link io.netty.util.ReferenceCounted#retain()}
 * since the reference goes beyond their control;
 */
package stupaq.cloudatlas.gossiping;
