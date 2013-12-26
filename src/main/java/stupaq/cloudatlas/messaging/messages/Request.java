package stupaq.cloudatlas.messaging.messages;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.Message;

@Immutable
public abstract class Request<ContextType> extends Message {
  private ContextType context;

  public ContextType context() {
    return context;
  }

  public Request<ContextType> attach(ContextType context) {
    Preconditions.checkNotNull(context);
    Preconditions.checkState(this.context == null);
    this.context = context;
    return this;
  }
}
