// Copyright (c) 2007 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.internal.editors.text;

public class Occurrence {

  private final int line;
  private final int column;
  private final int length;

  public Occurrence( final int line, final int column, final int length ) {
    this.line = line;
    this.column = column;
    this.length = length;
  }


  // attribute getters
  ////////////////////

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public int getLength() {
    return length;
  }
}
