package stupaq.cloudatlas.services.busybody.sessions;

import com.google.common.collect.Sets;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SessionIdTest {
  @Test
  public void testNextSession() throws Exception {
    assertEquals(new SessionId(), new SessionId());
    SessionId session = new SessionId();
    Set<SessionId> seen = Sets.newHashSet();
    int count = Short.MAX_VALUE - Short.MIN_VALUE + 1;
    for (int i = 0; i < count; i++) {
      assertTrue(seen.add(session));
      session = session.nextSession();
    }
    assertFalse(seen.add(session));
    assertEquals(new SessionId(), session);
  }
}
