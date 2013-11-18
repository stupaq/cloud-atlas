package stupaq.cloudatlas.interpreter.context;

import com.google.common.base.Preconditions;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType;

import java.util.HashSet;

import stupaq.cloudatlas.attribute.Attribute;
import stupaq.cloudatlas.attribute.AttributeName;
import stupaq.cloudatlas.interpreter.errors.EvaluationException;
import stupaq.cloudatlas.interpreter.types.RSingle;
import stupaq.cloudatlas.zone.ZoneManagementInfo;

public interface OutputContext {

  public void put(String attribute, RSingle<? extends AttributeType> value);

  public static class InnerSelectOutputContext implements OutputContext {
    @Override
    public void put(String attribute, RSingle<? extends AttributeType> value) {
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
    public void put(String name, RSingle<? extends AttributeType> value) {
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
    public void put(String attribute, RSingle<? extends AttributeType> value) {
      Preconditions.checkNotNull(attribute);
      Preconditions.checkNotNull(value);
      // Attribute value cannot start with reserved prefix
      AttributeName name = AttributeName.valueOf(attribute);
      destination.updateAttribute(new Attribute<>(name, value.or(null)));
    }
  }
}
