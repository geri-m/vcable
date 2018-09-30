package org.vcable.openvpn;

import java.io.IOException;

public interface Transiver {

  String transiveMultiLine(final String command) throws IOException;

  String transiveSingleLine(final String command) throws IOException;

}
