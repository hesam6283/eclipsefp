// Copyright (c) 2003-2008 by Leif Frenzel - see http://leiffrenzel.de
// This code is made available under the terms of the Eclipse Public License,
// version 1.0 (EPL). See http://www.eclipse.org/legal/epl-v10.html
package net.sf.eclipsefp.haskell.ui.internal.editors.haskell.text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sf.eclipsefp.haskell.ui.HaskellUIPlugin;
import net.sf.eclipsefp.haskell.ui.internal.preferences.editor.IEditorPreferenceNames;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


/** <p>Provides colors for syntax coloring in the editor.</p>
  *
  * <p>This is implemented as singleton to make it accessible from
  * everywhere and also to reduce resource management to one single place.</p>
  *
  * @author Leif Frenzel
  */
public class ColorProvider implements IEditorPreferenceNames {

  public static final RGB DEFAULT_COMMENT           = new RGB( 128, 128, 192 );
  public static final RGB DEFAULT_LITERATE_COMMENT  = new RGB( 128, 128, 192 );
  public static final RGB DEFAULT_KEYWORD           = new RGB( 0, 0, 196 );
  public static final RGB DEFAULT_FUNCTION          = new RGB( 64, 192, 192 );
  public static final RGB DEFAULT_STRING            = new RGB( 128, 64, 64 );
  public static final RGB DEFAULT_CHAR              = new RGB( 96, 96, 96 );
  public static final RGB DEFAULT_OTHER             = new RGB( 0, 0, 0 );

  /** the singleton instance of ColorProvider. */
  private static ColorProvider _instance;

  private final Map<RGB, Color> colors;
  private final Map<String, RGB> rgbs;

  private IPreferenceStore prefStore;

  /** <p>constructs the singleton instance of ColorProvider. Private in order
   * to ensure the singleton pattern.</p> */
  private ColorProvider() {
    colors = new HashMap<RGB, Color>( 10 );
    rgbs = new HashMap<String, RGB>( 10 );
    initializeRgbs();
  }

  public static synchronized ColorProvider getInstance() {
    if( _instance == null ) {
      _instance = new ColorProvider();
    }
    return _instance;
  }

  public ColorProvider(final IPreferenceStore store){
    prefStore=store;
    colors = new HashMap<RGB, Color>( 10 );
    rgbs = new HashMap<String, RGB>( 10 );
    initializeRgbs();
  }

  /** <p>releases all of the color resources held by this ColorProvider.</p> */
  public void dispose() {
    Iterator<Color> it = colors.values().iterator();
    while( it.hasNext() ) {
      it.next().dispose();
    }
  }

  public Color getColor( final String key ) {
    RGB rgb = rgbs.get( key );
    Assert.isNotNull( rgb );
    return getColor( rgb );
  }

  void changeColor( final String key, final Object newValue ) {
    RGB oldRgb = rgbs.get( key );
    if( oldRgb != null ) {
      RGB newRgb = getNewRgb( newValue );
      if( newRgb != null ) {
        rgbs.put( key, newRgb );
      }
    }
  }


  // helping methods
  //////////////////

  private RGB getNewRgb( final Object value ) {
    RGB result = null;
    if( value instanceof RGB ) {
      result = ( RGB )value;
    } else if( value instanceof String ) {
      result = StringConverter.asRGB( ( String )value );
    }
    return result;
  }

  private Color getColor( final RGB rgb ) {
    Color color = colors.get( rgb );
    if( color == null ) {
      color = new Color( Display.getCurrent(), rgb );
      colors.put( rgb, color );
    }
    return color;
  }

  private void initializeRgbs() {
    putRgb( EDITOR_COMMENT_COLOR, DEFAULT_COMMENT );
    putRgb( EDITOR_LITERATE_COMMENT_COLOR, DEFAULT_LITERATE_COMMENT );
    putRgb( EDITOR_FUNCTION_COLOR, DEFAULT_FUNCTION );
    putRgb( EDITOR_KEYWORD_COLOR, DEFAULT_KEYWORD );
    putRgb( EDITOR_DEFAULT_COLOR, DEFAULT_OTHER );
    putRgb( EDITOR_STRING_COLOR, DEFAULT_STRING );
    putRgb( EDITOR_CHAR_COLOR, DEFAULT_CHAR );
  }

  private void putRgb( final String key, final RGB defaultRgb ) {
    RGB rgb = PreferenceConverter.getColor( getPreferenceStore(), key );
    if( rgb == null ) {
      rgb = defaultRgb;
    }
    rgbs.put( key, rgb );
  }

  private IPreferenceStore getPreferenceStore() {
    if (prefStore!=null){
      return prefStore;
    }
    return HaskellUIPlugin.getDefault().getPreferenceStore();
  }
}