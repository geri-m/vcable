package org.vcable.openvpn.responses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Welcome implements Response {

  /*
   * >INFO:OpenVPN Management Interface Version 1 -- type 'help' for more info
   */

  private static final String WELCOME_REGEX = ">INFO:OpenVPN Management Interface Version (\\d+) -- type 'help' for more info";
  private static final Pattern WELCOME_PATTERN = Pattern.compile(WELCOME_REGEX);
  private final String message;
  private final int versionOfInterface;

  /**
   * Response Object of the Management Interface, after a connection was established.
   *
   * @param versionOfInterface Version Number of the Management Interface
   * @param message            Full Message that was received after the connection.
   */

  private Welcome(final int versionOfInterface, final String message) {
    this.versionOfInterface = versionOfInterface;
    this.message = message;
  }

  /**
   * Singleton to generated Object from Response
   *
   * @param message String after Connect to parse
   * @return Object to Create
   * @throws ResponseParseException Exception if communication was not successful
   */

  public static synchronized Welcome getInstance(final String message) throws ResponseParseException {
    final Matcher matcher = WELCOME_PATTERN.matcher(message);
    if (matcher.matches()) {
      return new Welcome(Integer.parseInt(matcher.group(1)), message);
    }
    throw new ResponseParseException("Unable to Create Welcome Object");
  }

  public String getMessage() {
    return message;
  }

  public int getVersionOfInterface() {
    return versionOfInterface;
  }
}
