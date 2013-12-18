package stupaq.cloudatlas.messaging;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Response<RequestType extends Request> extends Message {
  private RequestType request;

  public RequestType request() {
    return request;
  }

  public void attach(RequestType request) {
    Preconditions.checkNotNull(request);
    Preconditions.checkState(this.request == null);
    this.request = request;
  }
}
