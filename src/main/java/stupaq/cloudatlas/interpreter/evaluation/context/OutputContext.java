package stupaq.cloudatlas.interpreter.evaluation.context;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.values.RSingle;
import stupaq.cloudatlas.zone.ZoneManagementInfo;

public interface OutputContext {

  public void put(String attribute, RSingle<? extends AttributeValue> value);

  public void commit();

  public static class InnerSelectOutputContext implements OutputContext {
    @Override
    public void put(String attribute, RSingle<? extends AttributeValue> value) {
      throw new EvaluationException("Cannot set attribute in this context");
    }

    @Override
    public void commit() {
      // no-op
    }
  }

  public static class RedefinitionAwareOutputContext implements OutputContext {
    private final OutputContext outputContext;
    private final HashSet<String> alreadyDefined;

    public RedefinitionAwareOutputContext(OutputContext outputContext) {
      this.outputContext = outputContext;
      this.alreadyDefined = new HashSet<>();
    }

    @Override
    public void put(String name, RSingle<? extends AttributeValue> value) {
      if (!alreadyDefined.add(name)) {
        throw new EvaluationException("Attribute: " + name + " already defined");
      }
      outputContext.put(name, value);
    }

    @Override
    public void commit() {
      outputContext.commit();
    }
  }

  public static class ZMIUpdaterOutputContext implements OutputContext {
    private final ZoneManagementInfo destination;
    private final List<Attribute> putsLog;

    public ZMIUpdaterOutputContext(ZoneManagementInfo destination) {
      this.destination = destination;
      this.putsLog = new ArrayList<>();
    }

    @Override
    public void put(String nameStr, RSingle<? extends AttributeValue> value) {
      Preconditions.checkNotNull(nameStr);
      Preconditions.checkNotNull(value);
      // Attribute value cannot start with reserved prefix
      AttributeName name = AttributeName.valueOf(nameStr);
      Attribute attribute = new Attribute<>(name, value.get());
      putsLog.add(attribute);
    }

    @Override
    public void commit() {
      for (Attribute attribute : putsLog) {
        destination.updateAttribute(attribute);
      }
      putsLog.clear();
    }
  }
}
