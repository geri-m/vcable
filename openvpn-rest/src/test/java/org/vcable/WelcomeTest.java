package org.vcable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.vcable.openvpn.responses.ResponseParseException;
import org.vcable.openvpn.responses.Welcome;

public class WelcomeTest {

  @Test
  public void test01_parseWelcomeOkay() throws ResponseParseException {
    final String toTest = ">INFO:OpenVPN Management Interface Version 1 -- type 'help' for more info";
    final Welcome result = Welcome.getInstance(toTest);
    assertEquals(1, result.getVersionOfInterface());
    assertEquals(toTest, result.getMessage());
  }

  @Test
  public void parseWelcomeNoVersionNumber() {
    final String toTest = ">INFO:OpenVPN Management Interface Version n -- type 'help' for more info";
    try {
      Welcome.getInstance(toTest);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to Create Welcome Object", e.getMessage());
    }
  }

  @Test
  public void parseWelcomeNoCorrectString() {
    final String toTest = ">TEstString";
    try {
      Welcome.getInstance(toTest);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to Create Welcome Object", e.getMessage());
    }
  }
}
