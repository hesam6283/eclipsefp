package net.sf.eclipsefp.haskell.core.internal.doubles;

import java.util.ArrayList;
import java.util.List;
import net.sf.eclipsefp.haskell.core.halamo.ICompilationUnit;
import net.sf.eclipsefp.haskell.core.halamo.IDeclaration;
import net.sf.eclipsefp.haskell.core.halamo.IExportSpecification;
import net.sf.eclipsefp.haskell.core.halamo.IHaskellLanguageElement;
import net.sf.eclipsefp.haskell.core.halamo.IImport;
import net.sf.eclipsefp.haskell.core.halamo.IModule;
import net.sf.eclipsefp.haskell.core.halamo.ISourceLocation;

public class StubModule implements IModule {

	private final IDeclaration[] fDeclarations;
	private final String fModuleName;
	private final List<IImport> fImports = new ArrayList<IImport>();

	public StubModule() {
		this("UnimportantModule");
	}

	public StubModule(final String moduleName, final String... declNames) {
		fModuleName = moduleName;
		fDeclarations = new IDeclaration[declNames.length];
		for(int i = 0; i < declNames.length; ++i) {
			fDeclarations[i] = new StubDeclaration(declNames[i]);
		}
	}

	public IExportSpecification[] getExportSpecifications() {
		return null;
	}

	public IImport[] getImports() {
		return fImports.toArray(new IImport[fImports.size()]);
	}

	public IDeclaration[] getDeclarations() {
		return fDeclarations;
	}

	public ICompilationUnit getCompilationUnit() {
		return null;
	}

	public String getName() {
		return fModuleName;
	}

	public IHaskellLanguageElement getParent() {
		return null;
	}

	public ISourceLocation getSourceLocation() {
		return null;
	}

	public void addImport(final String module) {
		fImports.add(new StubImport(module));
	}

	public boolean isEmpty() {
		return true;
	}

}
