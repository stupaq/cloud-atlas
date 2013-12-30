package stupaq.commons.cache;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import io.netty.util.ReferenceCountUtil;

public class ReferenceCountedRemovalListener implements RemovalListener<Object, Object> {
  @Override
  public void onRemoval(RemovalNotification<Object, Object> notification) {
    try {
      ReferenceCountUtil.release(notification.getKey());
    } catch (Throwable ignored) {
    }
    try {
      ReferenceCountUtil.release(notification.getValue());
    } catch (Throwable ignored) {
    }
  }
}
