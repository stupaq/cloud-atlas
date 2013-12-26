package stupaq.cloudatlas.messaging.messages;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.Message;

@Immutable
public abstract class Response<RequestType extends Request> extends Message {
  private RequestType request;

  public RequestType request() {
    return request;
  }

  public Response<RequestType> attach(RequestType request) {
    Preconditions.checkNotNull(request);
    Preconditions.checkState(this.request == null);
    this.request = request;
    return this;
  }
}
