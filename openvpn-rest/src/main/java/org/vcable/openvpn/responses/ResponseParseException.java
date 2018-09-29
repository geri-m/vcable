package org.vcable.openvpn.responses;

public class ResponseParseException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Create a ResponseParseException instance with the specified message.
   *
   * @param message the message for the exception
   */
  public ResponseParseException(final String message) {
    super(message);
  }

  /**
   * Create a ResponseParseException instance based on the exception.
   *
   * @param exception the Exception to wrap
   */
  public ResponseParseException(final Exception exception) {
    super(exception);
  }


}
