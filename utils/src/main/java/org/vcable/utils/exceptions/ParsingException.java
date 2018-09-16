package org.vcable.utils.exceptions;


public class ParsingException extends Exception {

  private static final long serialVersionUID = -1432798352142555274L;

  public ParsingException() {
    this("New ParsingException");
  }

  public ParsingException(final String ex) {
    super(ex);
  }


}
