package stupaq.cloudatlas.runnable;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.Service.State;

import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;

import stupaq.cloudatlas.serialization.CATypeRegistry;

public class GenericServiceRunner {
  static {
    CATypeRegistry.registerCATypes();
  }

  private static final Log LOG = LogFactory.getLog(GenericServiceRunner.class);

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    int status = 0;
    try {
      Preconditions.checkArgument(args.length > 0, "Provide service to run");
      Class<? extends Service> clazz = (Class<? extends Service>) Class.forName(args[0]);
      String[] serviceArgs = new String[args.length - 1];
      System.arraycopy(args, 1, serviceArgs, 0, args.length - 1);
      LOG.info("Initializing service " + clazz.getSimpleName() + " with arguments: " +
          Arrays.asList(serviceArgs));
      final Service process =
          (Service) ConstructorUtils.getAccessibleConstructor(clazz, String[].class)
              .newInstance(new Object[]{serviceArgs});
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
          LOG.info("Stopping service");
          process.stopAsync().awaitTerminated();
        }
      }));
      LOG.info("Starting service");
      process.startAsync().awaitTerminated();
      if (process.state() == State.FAILED) {
        LOG.error("Service exited abnormally", process.failureCause());
        status = -2;
      }
    } catch (Exception e) {
      LOG.fatal("Exception in " + GenericServiceRunner.class.getSimpleName(), e);
      status = -1;
    }
    System.exit(status);
  }
}
