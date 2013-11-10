package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Preconditions;

import java.util.Collection;

import stupaq.cloudatlas.interpreter.Value;
import stupaq.cloudatlas.interpreter.errors.OperationNotApplicable;

public class TypeUtils {
  static void assertUniformCollection(Collection<? extends Value> collection) {
    if (!collection.isEmpty()) {
      Class clazz = collection.iterator().next().getType();
      for (Value elem : collection) {
        Preconditions.checkState(elem.getType() == clazz,
            "Collection contains elements of not matching type");
      }
    }
  }

  static void assertSameType(Value a, Value b) {
    if (a.getType() != b.getType()) {
      throw new OperationNotApplicable(
          "Cannot compare: " + a.getType().getSimpleName() + " with: " + b.getType()
              .getSimpleName());
    }
  }
}
