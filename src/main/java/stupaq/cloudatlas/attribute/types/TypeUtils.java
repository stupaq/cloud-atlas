package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Preconditions;

import java.util.Collection;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.interpreter.errors.UndefinedOperationException;

// TODO dissolve this
public class TypeUtils {
  static void assertUniformCollection(Collection<? extends AttributeValue> collection) {
    if (!collection.isEmpty()) {
      Class clazz = collection.iterator().next().getType();
      for (AttributeValue elem : collection) {
        Preconditions.checkState(elem.getType() == clazz,
            "Collection contains elements of not matching type");
      }
    }
  }

  static void assertSameType(AttributeValue a, AttributeValue b) {
    if (a.getType() != b.getType()) {
      throw new UndefinedOperationException(
          "Cannot compare: " + a.getType().getSimpleName() + " with: " + b.getType()
              .getSimpleName());
    }
  }
}
