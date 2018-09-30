package org.vcable.openvpn.responses;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vcable.openvpn.OpenVpnCommandEnum;
import org.vcable.openvpn.Transceiver;

public class Version implements Response {

  /*
   * OpenVPN Version: OpenVPN 2.4.6 x86_64-alpine-linux-musl [SSL (OpenSSL)] [LZO] [LZ4] [EPOLL] [MH/PKTINFO] [AEAD] built on Jul  8 2018
   * Management Version: 1
   * END
   */

  private static final String REGEX = "OpenVPN Version: (.+)Management Version: (\\d*).*END";
  private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.DOTALL);

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

  /**
   * Singleton to fire command and generated Object from Response
   *
   * @param transceiver Link to Management Console
   * @return Object to Create
   * @throws ResponseParseException Exception if communication was not successful
   */

  public static synchronized Version getInstance(final Transceiver transceiver) throws ResponseParseException {
    final Matcher version;
    try {
      version = PATTERN.matcher(transceiver.transceiverMultiLine(OpenVpnCommandEnum.VERSION));
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

  /**
   * Get Version String from OpenVpn
   *
   * @return Version String
   */

  public String getOpenVpnVersion() {
    return openVpnVersion;
  }

  /**
   * Get Version Number from OpenVpn Management Console
   *
   * @return Version Number
   */

  public int getVersionOfInterface() {
    return versionOfInterface;
  }

}
