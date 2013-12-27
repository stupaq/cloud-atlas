package stupaq.cloudatlas.messaging.messages.requests;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.messages.Message;

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
