package org.vcable.openvpn.responses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Welcome implements Response {

  private static final String WELCOME_REGEX = ">INFO:OpenVPN Management Interface Version (\\d+) -- type 'help' for more info";
  private static final Pattern WELCOME_PATTERN = Pattern.compile(WELCOME_REGEX);
  private final String message;
  private final int versionOfInterface;

  private Welcome(final int versionOfInterface, final String message) {
    this.versionOfInterface = versionOfInterface;
    this.message = message;
  }

  public static Welcome getInstance(final String message) throws ResponseParseException {
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
