package org.vcable;

import org.vcable.openvpn.responses.ResponseParseException;
import org.vcable.openvpn.responses.Welcome;
import junit.framework.TestCase;

public class WelcomeTest extends TestCase {

  public void test01_parseWelcomeOkay() {
    final String toTest = ">INFO:OpenVPN Management Interface Version 1 -- type 'help' for more info";
    try {
      final Welcome result = Welcome.getInstance(toTest);
      assertEquals(1, result.getVersionOfInterface());
      assertEquals(toTest, result.getMessage());
    } catch (final ResponseParseException e) {
      fail();
    }
  }

  public void test02_parseWelcomeNoVersionNumber() {
    final String toTest = ">INFO:OpenVPN Management Interface Version n -- type 'help' for more info";
    try {
      Welcome.getInstance(toTest);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to Create Welcome Object", e.getMessage());
    }
  }

  public void test03_parseWelcomeNoCorrectString() {
    final String toTest = ">TEstString";
    try {
      Welcome.getInstance(toTest);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to Create Welcome Object", e.getMessage());
    }
  }
}
