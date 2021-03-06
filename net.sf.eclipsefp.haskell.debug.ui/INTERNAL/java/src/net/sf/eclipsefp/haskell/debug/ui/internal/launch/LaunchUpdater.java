package net.sf.eclipsefp.haskell.debug.ui.internal.launch;

import java.util.ArrayList;
import java.util.List;
import net.sf.eclipsefp.haskell.debug.core.internal.launch.ILaunchAttributes;
import net.sf.eclipsefp.haskell.ui.HaskellUIPlugin;
import net.sf.eclipsefp.haskell.ui.util.CabalFileChangeListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * Listener to update launch configurations when Cabal file changes
 * @author JP Moresmau
 *
 */
public class LaunchUpdater implements CabalFileChangeListener {

  public void cabalFileChanged( final IFile cabalF ) {
    try {
      final ClassLoader cl=getClass().getClassLoader();
     String projectName=cabalF.getProject().getName();
     for(ILaunchConfiguration c:LaunchOperation.getConfigurationsForProject( projectName )){
       String delegateClass=c.getAttribute( ILaunchAttributes.DELEGATE, (String)null );
       if (delegateClass!=null){
         try {

           IInteractiveLaunchOperationDelegate delegate=(IInteractiveLaunchOperationDelegate)cl.loadClass( delegateClass).newInstance();

           List<?> fileNames=c.getAttribute( ILaunchAttributes.FILES, new ArrayList<Object>() );
           IFile[] files=new IFile[fileNames.size()];
           int ix=0;
           for(Object o:fileNames){
             IFile f=(IFile)cabalF.getProject().findMember( (String )o);
             files[ix++]=f;
           }
           String args=InteractiveLaunchOperation.getArguments(delegate,cabalF.getProject(),files);
           ILaunchConfigurationWorkingCopy wc=c.getWorkingCopy();
           wc.setAttribute( ILaunchAttributes.ARGUMENTS,args);
           wc.doSave();
         } catch (Throwable t){
           HaskellUIPlugin.log( t );
         }
       }
     }

    } catch (CoreException ce){
      HaskellUIPlugin.log( ce );
    }
  }
}

