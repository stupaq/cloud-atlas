package stupaq.cloudatlas.runnable.agent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ServiceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;

import stupaq.cloudatlas.module.rmiserver.RMIServer;

@SuppressWarnings("unused")
public final class CAAgentProcess extends AbstractIdleService {
  private static final Log LOG = LogFactory.getLog(CAAgentProcess.class);
  private final ServiceManager modules;

  public CAAgentProcess(String[] args) {
    Preconditions.checkArgument(args.length >= 1, "Missing arguments: leaf zone");
    modules = new ServiceManager(Arrays.asList(new RMIServer()));
  }

  @Override
  protected void startUp() throws Exception {
    modules.startAsync().awaitHealthy();
  }

  @Override
  protected void shutDown() {
    modules.stopAsync().awaitStopped();
  }
}

