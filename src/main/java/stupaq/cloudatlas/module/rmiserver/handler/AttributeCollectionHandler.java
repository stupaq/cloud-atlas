package stupaq.cloudatlas.module.rmiserver.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import stupaq.cloudatlas.bus.messages.AttributesUpdateMessage;
import stupaq.cloudatlas.module.rmiserver.protocol.AttributeCollectionProtocol;
import stupaq.compact.SerializableWrapper;

public class AttributeCollectionHandler implements AttributeCollectionProtocol {
  private static final Log LOG = LogFactory.getLog(AttributeCollectionHandler.class);

  @Override
  public void collect(SerializableWrapper<AttributesUpdateMessage> message) {
    if (LOG.isInfoEnabled()) {
      // String concatenation here is eager and very expensive
      LOG.info("Received: " + message.get());
    }
    // FIXME
  }
}
