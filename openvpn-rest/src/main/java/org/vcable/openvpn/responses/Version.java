package org.vcable.openvpn.responses;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vcable.openvpn.Transiver;

public class Version implements Response {

  /*
   * OpenVPN Version: OpenVPN 2.4.6 x86_64-alpine-linux-musl [SSL (OpenSSL)] [LZO] [LZ4] [EPOLL] [MH/PKTINFO] [AEAD] built on Jul  8 2018
   * Management Version: 1
   * END
   */

  private static final String VERSION_REGEX = "OpenVPN Version: (.+)Management Version: (\\d*).*END";
  private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEX, Pattern.DOTALL);
  private static final String CMD = "version";

  private final String openVpnVersion;
  private final int versionOfInterface;

  /**
   * Response Object from the Management interface on a 'version' command.
   *
   * @param openVpnVersion     Version String of OpenVpn
   * @param versionOfInterface Version Number of the Management Interface
   */

  private Version(final String openVpnVersion, final int versionOfInterface) {
    this.versionOfInterface = versionOfInterface;
    this.openVpnVersion = openVpnVersion;
  }

  public static synchronized Version getInstance(final Transiver transiver) throws ResponseParseException {
    final Matcher version;
    try {
      version = VERSION_PATTERN.matcher(transiver.transiveMultiLine(CMD));
      if (version.matches()) {
        return new Version(version.group(1)
            .trim(), Integer.parseInt(version.group(2)
            .trim()));
      }
    } catch (final IOException | NumberFormatException e) {
      throw new ResponseParseException(e);
    }
    throw new ResponseParseException("Unable to parse Version Object");
  }

  public String getOpenVpnVersion() {
    return openVpnVersion;
  }

  public int getVersionOfInterface() {
    return versionOfInterface;
  }

}
