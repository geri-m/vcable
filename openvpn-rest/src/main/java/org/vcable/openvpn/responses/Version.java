package org.vcable.openvpn.responses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Response {

  /*
   * OpenVPN Version: OpenVPN 2.4.6 x86_64-alpine-linux-musl [SSL (OpenSSL)] [LZO] [LZ4] [EPOLL] [MH/PKTINFO] [AEAD] built on Jul  8 2018
   * Management Version: 1
   * END
   */

  private static final String VERSION_REGEX = "OpenVPN Version: (.+)Management Version: (\\d*).*END";
  private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEX, Pattern.DOTALL);

  private final String message;
  private final String openVpnVersion;
  private final int versionOfInterface;

  private Version(final String openVpnVersion, final int versionOfInterface, final String message) {
    this.versionOfInterface = versionOfInterface;
    this.openVpnVersion = openVpnVersion;
    this.message = message;
  }

  public static Version getInstance(final String message) throws ResponseParseException {
    final Matcher version = VERSION_PATTERN.matcher(message);
    if (version.matches()) {
      try {
        return new Version(version.group(1)
            .trim(), Integer.parseInt(version.group(2)
            .trim()), message.trim());
      } catch (final NumberFormatException nfe) {
        throw new ResponseParseException(nfe);
      }
    }
    throw new ResponseParseException("Unable to parse Version Object");
  }

  public String getMessage() {
    return message;
  }

  public String getOpenVpnVersion() {
    return openVpnVersion;
  }

  public int getVersionOfInterface() {
    return versionOfInterface;
  }

}
