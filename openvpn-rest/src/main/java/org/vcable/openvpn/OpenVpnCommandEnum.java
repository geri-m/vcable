package org.vcable.openvpn;

public enum OpenVpnCommandEnum {

  // Commands must be lower case.
  VERSION("version"),
  PID("pid");

  private final String command;

  private OpenVpnCommandEnum(final String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }
}
