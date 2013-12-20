package stupaq.cloudatlas.services.scribe;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.naming.EntityName;
import stupaq.commons.base.Exceptions;

public class RecordsManager implements AttributesScribeConfigKeys, AutoCloseable {
  private static final Log LOG = LogFactory.getLog(RecordsManager.class);
  private static final String EXTENSION = ".txt";
  private final File directory;
  private final Map<EntityName, PrintWriter> writers = Maps.newHashMap();

  public RecordsManager(CAConfiguration config) {
    directory = new File(config.getString(DIRECTORY, DIRECTORY_DEFAULT));
  }

  @Override
  public void close() {
    Exceptions.cleanup(LOG, writers.values());
  }

  public Records forEntity(EntityName entity) throws IOException {
    PrintWriter writer = writers.get(entity);
    if (writer == null) {
      File file = new File(directory, entity + EXTENSION);
      Files.createParentDirs(file);
      writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
      writers.put(entity, writer);
    }
    return new Records(writer);
  }

  public class Records implements AutoCloseable {
    private final PrintWriter writer;

    protected Records(PrintWriter writer) {
      this.writer = writer;
    }

    public void record(long timestamp, AttributeValue value) {
      writer.println(timestamp + "\t" + value);
      writer.flush();
    }

    @Override
    public void close() {
      // RecordsManager will handle this more efficiently
    }
  }
}
