package stupaq.cloudatlas.configuration;

import com.google.common.util.concurrent.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ServicesList extends ArrayList<Service> {
  private static final Log LOG = LogFactory.getLog(ServicesList.class);

  public void addWith(BootstrapConfiguration config, Class<? extends Service> clazz)
      throws Exception {
    StartIfPresent condition = clazz.getAnnotation(StartIfPresent.class);
    if (condition != null && config.subset(condition.section()).isEmpty()) {
      LOG.info("Service: " + clazz.getSimpleName() + " will not be started, missing: " +
          condition.section());
    } else {
      LOG.info("Service: " + clazz.getSimpleName() + " will be started");
      try {
        add(clazz.getDeclaredConstructor(BootstrapConfiguration.class).newInstance(config));
      } catch (InvocationTargetException e) {
        throw e.getTargetException() instanceof Exception ? (Exception) e.getTargetException() : e;
      }
    }
  }
}
