// Copyright (c) 2003-2004 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.common.ui.preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sf.eclipsefp.common.ui.util.DialogUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;




/** <p>the super class for all tabs on a tabbed preference page.</p>
  *
  * @author Leif Frenzel
  */
public abstract class Tab implements IPropertyChangeListener{

  private final IPreferenceStore preferenceStore;
  private final Map<Button, String> checkBoxes = new HashMap<Button, String>();
  private final Map<Control, Control> labels = new HashMap<Control, Control>();
  private final Map<Text, String> textFields = new HashMap<Text, String>();

  private final SelectionListener fCheckBoxListener = new SelectionAdapter() {
    @Override
    public void widgetSelected( final SelectionEvent event ) {
      Button button = ( Button )event.widget;
      String key = checkBoxes.get( button );
      getPreferenceStore().setValue( key, button.getSelection() );
    }
  };

  private final ModifyListener fTextFieldListener = new ModifyListener() {
    public void modifyText( final ModifyEvent event ) {
      Text text = ( Text )event.widget;
      String key = textFields.get( text );
      getPreferenceStore().setValue( key, text.getText() );
    }
  };

  public Tab( final IPreferenceStore store ) {
    this.preferenceStore = store;
    this.preferenceStore.addPropertyChangeListener(this);
  }

  public abstract Control createControl( Composite parent );

  public void dispose(){
    if (this.preferenceStore!=null){
      this.preferenceStore.removePropertyChangeListener(this);
    }
  }

  public void initializeFields() {
    initializeCheckboxes();
    initializeTexts();
  }

  protected Control getLabel( final Control field ) {
    return labels.get( field );
  }

  protected IPreferenceStore getPreferenceStore() {
    return preferenceStore;
  }


  // UI creation helping methods for subclasses
  /////////////////////////////////////////////

  protected void createLabel( final Composite parent, final String text ) {
    Label label = new Label( parent, SWT.LEFT );
    label.setText( text );
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.BEGINNING;
    label.setLayoutData( gridData );
  }

  protected final Button addBooleanField( final Composite parent,
                                          final String label,
                                          final String key,
                                          final int indentation ) {
    Button checkBox = new Button( parent, SWT.CHECK );
    checkBox.setText( label );

    GridData gd = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING );
    gd.horizontalIndent = indentation;
    gd.horizontalSpan = 2;
    checkBox.setLayoutData( gd );
    checkBox.addSelectionListener( fCheckBoxListener );

    checkBoxes.put( checkBox, key );

    return checkBox;
  }

  protected Control addIntegerField( final Composite composite,
                                     final String label,
                                     final String key,
                                     final int textLimit,
                                     final int indentation ) {
    Text result = addStringField( composite, label, textLimit, indentation );
    textFields.put( result, key );
    return result;
  }

  protected Control addTextField( final Composite composite,
                                  final String label,
                                  final String key,
                                  final int textLimit,
                                  final int indentation ) {
    Text result = addStringField( composite, label, textLimit, indentation );
    textFields.put( result, key );
    result.addModifyListener( fTextFieldListener );
    return result;
  }

  // helping methods
  //////////////////

  private Text addStringField( final Composite composite,
                               final String label,
                               final int textLimit,
                               final int indentation ) {
    Label labelControl = new Label( composite, SWT.NONE );
    labelControl.setText( label );
    GridData gd = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING );
    gd.horizontalIndent = indentation;
    labelControl.setLayoutData( gd );

    Text textControl = new Text( composite, SWT.BORDER | SWT.SINGLE );
    gd = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING );
    gd.widthHint = DialogUtil.convertWidthInCharsToPixels( textControl,
                                                           textLimit + 1 );
    textControl.setLayoutData( gd );
    textControl.setTextLimit( textLimit );

    labels.put( textControl, labelControl );
    return textControl;
  }

  private void initializeTexts() {
    Iterator<Text> iter = textFields.keySet().iterator();
    while( iter.hasNext() ) {
      Text text = iter.next();
      String key = textFields.get( text );
      text.setText( getPreferenceStore().getString( key ) );
    }
  }

  private void initializeCheckboxes() {
    Iterator<Button> iter = checkBoxes.keySet().iterator();
    while( iter.hasNext() ) {
      Button button = iter.next();
      String key = checkBoxes.get( button );
      button.setSelection( getPreferenceStore().getBoolean( key ) );
    }
  }
}