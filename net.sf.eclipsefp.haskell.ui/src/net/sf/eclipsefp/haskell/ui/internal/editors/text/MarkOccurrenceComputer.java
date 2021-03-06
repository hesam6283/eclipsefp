// Copyright (c) 2007 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.internal.editors.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.eclipsefp.haskell.ui.internal.editors.haskell.HaskellEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

/** <p>computes identifier occurrences for highlighting in a given Haskell
  * source editor.</p>
  *
  * @author Leif Frenzel
  */
public class MarkOccurrenceComputer {

  private static final String ANNOTATION_TYPE
    = "net.sf.eclipsefp.haskell.ui.occurrences"; //$NON-NLS-1$

  private final HaskellEditor editor;
  private IDocument document;
  private final IMarkOccurrences markOccurrences;

  public MarkOccurrenceComputer( final HaskellEditor editor,
                                 final IMarkOccurrences markOccurrences ) {
    if( editor == null || markOccurrences == null ) {
      throw new IllegalArgumentException();
    }
    this.editor = editor;
    this.markOccurrences = markOccurrences;
  }

  public void setDocument( final IDocument document ) {
    this.document = document;
  }

  public void compute() {
    String content = document.get();
    ISelection selection = editor.getSelectionProvider().getSelection();
    if( selection instanceof ITextSelection ) {
      ITextSelection textSelection = ( ITextSelection )selection;
      try {
        IAnnotationModel model = getAnnotationModel( editor );
        if( model instanceof IAnnotationModelExtension ) {
          int offset = textSelection.getOffset();
          int line = document.getLineOfOffset( offset ); // zero-based
          int col = computeColumn( document, offset, line );
          Occurrence[] occs = markOccurrences.mark( content, line + 1, col );
          Map<Annotation, Position> map = computeAnnotations( occs );
          IAnnotationModelExtension amx = ( IAnnotationModelExtension )model;
          amx.replaceAnnotations( collectToRemove( model ), map );
        }
      } catch( final BadLocationException badlox ) {
        // this means we could not properly get information from the
        // editor document; no point in continuing
      }
    }
  }


  // helping functions
  ////////////////////

  private Annotation[] collectToRemove( final IAnnotationModel model ) {
    List<Annotation> result = new ArrayList<Annotation>();
    Iterator iterator = model.getAnnotationIterator();
    while( iterator.hasNext() ) {
      Object next = iterator.next();
      if( next instanceof Annotation ) {
        Annotation annotation = ( Annotation )next;
        if( ANNOTATION_TYPE.equals( annotation.getType() ) ) {
          result.add( annotation );
        }
      }
    }
    return result.toArray( new Annotation[ result.size() ] );
  }

  private IAnnotationModel getAnnotationModel( final HaskellEditor editor ) {
    IAnnotationModel result = null;
    if( editor != null ) {
      IDocumentProvider documentProvider = editor.getDocumentProvider();
      IEditorInput input = editor.getEditorInput();
      result = documentProvider.getAnnotationModel( input );
    }
    return result;
  }

  private int computeColumn( final IDocument document,
                             final int offset,
                             final int line ) throws BadLocationException {
    return ( offset - document.getLineOffset( line ) + 1 );
  }

  private List<Position> computePositions( final Occurrence[] occurrences ) {
    List<Position> result = new ArrayList<Position>();
    for( Occurrence occ: occurrences ) {
      try {
        int offs = document.getLineOffset( occ.getLine() - 1 );
        offs += occ.getColumn() - 1;
        result.add( new Position( offs, occ.getLength() ) );
      } catch( final BadLocationException badlox ) {
        // ignore that occurrence then
      }
    }
    return result;
  }

  private Map<Annotation, Position> computeAnnotations( final Occurrence[] occs ) {
    Map<Annotation, Position> result = new HashMap<Annotation, Position>();
    List<Position> poss = computePositions( occs );
    Iterator<Position> it = poss.iterator();
    while( it.hasNext() ) {
      Position pos = it.next();
      result.put( new Annotation( ANNOTATION_TYPE, false, "" ), pos ); //$NON-NLS-1$
    }
    return result;
  }
}
