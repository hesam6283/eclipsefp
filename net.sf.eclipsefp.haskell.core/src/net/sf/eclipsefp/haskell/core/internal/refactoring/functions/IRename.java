// Copyright (c) 2008 by Leif Frenzel - see http://leiffrenzel.de
// This code is made available under the terms of the Eclipse Public License,
// version 1.0 (EPL). See http://www.eclipse.org/legal/epl-v10.html
package net.sf.eclipsefp.haskell.core.internal.refactoring.functions;

/** <p>interface to the Haskell code that performs the Rename refactoring.</p>
  *
  * @author Leif Frenzel
  */
public interface IRename {
  String performRename( String content );
}