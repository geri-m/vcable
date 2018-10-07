package org.vcable.openvpn;

public enum OpenVpnCommandEnum {

  // Commands must be lower case.
  VERSION("version"),
  PID("pid"),
  STATUS("status");

  private final String command;

  OpenVpnCommandEnum(final String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }
}
