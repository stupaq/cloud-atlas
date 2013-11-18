package stupaq.cloudatlas.interpreter.context;

import com.google.common.base.Preconditions;

import java.util.HashSet;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.types.RSingle;
import stupaq.cloudatlas.zone.ZoneManagementInfo;

public interface OutputContext {

  public void put(String attribute, RSingle<? extends AttributeValue> value);

  public static class InnerSelectOutputContext implements OutputContext {
    @Override
    public void put(String attribute, RSingle<? extends AttributeValue> value) {
      throw new EvaluationException("Cannot set attribute in this context");
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
  }

  public static class ZMIUpdaterOutputContext implements OutputContext {
    private final ZoneManagementInfo destination;

    public ZMIUpdaterOutputContext(ZoneManagementInfo destination) {
      this.destination = destination;
    }

    @Override
    public void put(String nameStr, RSingle<? extends AttributeValue> value) {
      Preconditions.checkNotNull(nameStr);
      Preconditions.checkNotNull(value);
      // Attribute value cannot start with reserved prefix
      AttributeName name = AttributeName.valueOf(nameStr);
      Attribute attribute = new Attribute<>(name, value.or(null));
      destination.updateAttribute(attribute);
    }
  }
}
