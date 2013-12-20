package stupaq.cloudatlas.services.scribe;

import com.google.common.base.CharMatcher;

import org.junit.Test;

import stupaq.cloudatlas.attribute.AttributeName;

import static org.junit.Assert.assertTrue;

public class EntityTest {
  @Test
  public void testDelimiter() throws Exception {
    assertTrue(CharMatcher.anyOf(AttributeName.FORBIDDEN).matchesAllOf(Entity.DELIMITER));
  }
}
