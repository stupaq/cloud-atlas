package stupaq.cloudatlas.attribute.types;

import com.google.common.base.Preconditions;

import java.util.Collection;

import stupaq.cloudatlas.interpreter.Value;

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
}
