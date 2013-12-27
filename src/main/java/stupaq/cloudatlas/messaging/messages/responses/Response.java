package stupaq.cloudatlas.messaging.messages.responses;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.messages.Message;
import stupaq.cloudatlas.messaging.messages.requests.Request;

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
