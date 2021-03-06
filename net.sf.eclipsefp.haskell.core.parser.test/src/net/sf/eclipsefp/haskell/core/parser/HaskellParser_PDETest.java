// Copyright (c) 2004-2005 by Leif Frenzel
// See http://leiffrenzel.de
package net.sf.eclipsefp.haskell.core.parser;


import org.eclipse.core.runtime.CoreException;

import net.sf.eclipsefp.haskell.core.parser.test.util.Parser_PDETestCase;


import net.sf.eclipsefp.haskell.core.halamo.*;

/** <p>tests for the Haskell parser.</p>
  *
  * @author Leif Frenzel
  */
public class HaskellParser_PDETest extends Parser_PDETestCase {

  public void testSimpleRead() throws Exception {
    final String input = "-- testing module for a simple case\n" +
 			    		 "module Main where\n" +
 			    		 "\n" +
			    		 "main = print 42";
	ICompilationUnit cu = parseAsFile(input);
    assertTrue( cu != null );
    
    IModule[] modules = cu.getModules();
    assertTrue( modules.length == 1 );
    assertEquals( "Main", modules[ 0 ].getName() );
    ISourceLocation srcLoc = modules[ 0 ].getSourceLocation();
    assertEquals( 1, srcLoc.getLine() );
    assertEquals( 0, srcLoc.getColumn() );
  }
  
  public void testReadImports() throws Exception {
    final String input = "-- testing module for a simple case\n" +
    		             "module Main where\n" +
    		             "\n" +
    		             "import Foreign\n" +
    		             "import Language.Haskell.Parser\n" +
    		             "import Language.Haskell.Syntax\n" +
    		             "\n" +
    		             "import CString\n" +
    		             "\n" +
    		             "main = print 42";
    IModule module = parseModule(input);
    IImport[] imports = module.getImports();
    assertTrue ( imports != null );
    assertEquals( 4, imports.length );
    
    assertEquals( "Foreign", imports[ 0 ].getImportedElement() );
    assertEquals( 3, imports[ 0 ].getSourceLocation().getLine() );
    assertEquals( 0, imports[ 0 ].getSourceLocation().getColumn() );

    assertEquals( "Language.Haskell.Parser", imports[ 1 ].getImportedElement() );
    assertEquals( 4, imports[ 1 ].getSourceLocation().getLine() );
    assertEquals( 0, imports[ 1 ].getSourceLocation().getColumn() );

    assertEquals( "Language.Haskell.Syntax", imports[ 2 ].getImportedElement() );
    assertEquals( 5, imports[ 2 ].getSourceLocation().getLine() );
    assertEquals( 0, imports[ 2 ].getSourceLocation().getColumn() );
    
    assertEquals( "CString", imports[ 3 ].getImportedElement() );
    assertEquals( 7, imports[ 3 ].getSourceLocation().getLine() );
    assertEquals( 0, imports[ 3 ].getSourceLocation().getColumn() );
  }
  
  public void testIgnoreError() throws Exception {
	final String input = "-- testing module for an error case\n" +
	   		             "modu le Main where\n" +
	   		             "\n" +
	   		             "main = print 42";
	IModule module = parseModule(input);
	IDeclaration[] decls = module.getDeclarations();
    assertEquals(1, decls.length);
    
	IFunctionBinding fb = (IFunctionBinding) decls[0];
    assertEquals("main", fb.getName());
  }
  
  public void testSimpleDecls() throws Exception {
	final String input = "-- testing module with some declarations\n" +
			             "module Main where\n" +
			             "\n" +
			             "import Bla\n" +
			             "\n" +
			             "sabber :: Int -> Int\n" +
			             "sabber n = n + 42\n" +
			             "\n" +
			             "laber :: Int\n" +
			             "laber = 42";
	IModule module = parseModule(input);
    IImport[] imports = module.getImports();
    assertEquals( 1, imports.length );
    
    IDeclaration[] decls = module.getDeclarations();
    assertEquals( 4, decls.length );
  }
  
  public void testTypeSignatures() throws Exception {
	final String input = "-- testing module with some declarations\n" +
						 "module Main where\n" +
						 "\n" +
						 "import Bla\n" +
						 "\n" +
						 "idf1 :: Int -> Int\n" +
						 "idf1 n = n + 42\n" +
						 "\n" +
						 "idf2, idf3 :: Int\n" +
						 "idf2 = 42";
	IModule module = parseModule(input);
    IDeclaration[] decls = module.getDeclarations();
    assertEquals( 4, decls.length );
    
    ITypeSignature ts1 = ( ITypeSignature )decls[ 0 ];
    assertEquals( 1, ts1.getIdentifiers().length );
    assertEquals( "idf1", ts1.getIdentifiers()[ 0 ] );

    IFunctionBinding fb1 = ( IFunctionBinding )decls[ 1 ];
    assertEquals( 1, fb1.getMatches().length );
    IMatch match = fb1.getMatches()[ 0 ];
    assertEquals( "idf1", match.getName() );
    assertEquals( 6, match.getSourceLocation().getLine() );
    assertEquals( 0, match.getSourceLocation().getColumn() );
    
    ITypeSignature ts2 = ( ITypeSignature )decls[ 2 ];
    assertEquals( 2, ts2.getIdentifiers().length );
    assertEquals( "idf2", ts2.getIdentifiers()[ 0 ] );
    assertEquals( "idf3", ts2.getIdentifiers()[ 1 ] );
  }
  
  public void testPatternBidings() throws Exception {
	final String input = "-- testing module with some declarations\n" +
		                 "module Main where\n" +
		                 "\n" +
		                 "import Bla\n" +
		                 "\n" +
		                 "idf1 :: Int -> Int\n" +
		                 "idf1 n = n + 42\n" +
		                 "\n" +
		                 "idf2, idf3 :: Int\n" +
		                 "idf2 = 42";
	IModule module = parseModule(input);
	IDeclaration[] decls = module.getDeclarations();
	assertEquals( 4, decls.length );

	IPatternBinding pb1 = ( IPatternBinding )decls[ 3 ];
	assertEquals( 9, pb1.getSourceLocation().getLine() );
	assertEquals( 0, pb1.getSourceLocation().getColumn() );
  }
  
  public void testSimpleExports() throws Exception {
	final String input = "-- testing module with some export specifications\n" +
			             "module Main (idf1, Bla) where\n" +
			             "\n" +
			             "import Bla\n" +
			             "\n" +
			             "idf1 :: Int -> Int\n" +
			             "idf1 n = n + 42";
    IModule module = parseModule(input);
    IExportSpecification[] exports = module.getExportSpecifications();
    assertEquals( 2, exports.length );
    
    IExportVariable exportVar = ( IExportVariable )exports[ 0 ];
    assertEquals( "idf1", exportVar.getName() );
    IExportAbsolute exportAbs = ( IExportAbsolute )exports[ 1 ];
    assertEquals( "Bla", exportAbs.getName() );
  }
  
  public void testMoreExports() throws Exception {
	final String input = "-- testing module with some export specifications\n" +
				         "module Main (idf1, module Bla) where\n" +
				         "\n" +
				         "import Bla\n" +
				         "\n" +
				         "idf1 :: Int -> Int\n" +
				         "idf1 n = n + 42";
	
    IModule module = parseModule(input);
    IExportSpecification[] exports = module.getExportSpecifications();
    assertEquals( 2, exports.length );
    
    IExportVariable exportVar = ( IExportVariable )exports[ 0 ];
    assertEquals( "idf1", exportVar.getName() );

    IExportModuleContent emc = ( IExportModuleContent )exports[ 1 ];
    assertEquals( "Bla", emc.getName() );
  }

  public void testExportWildcard() throws Exception {
	final String input = "-- testing module with some export specifications\n" +
			             "module Main ( Blubb(..) ) where\n" +
			             "\n" +
			             "import Bla\n" +
			             "\n" +
			             "data Blubb = Blibb | Blabb | Bloob";

    IModule module = parseModule(input);
    IExportSpecification[] exports = module.getExportSpecifications();
    assertEquals( 1, exports.length );
    IExportThingAll exportTA = ( IExportThingAll )exports[ 0 ];
    assertEquals( "Blubb", exportTA.getName() );
  }

  public void testExportList() throws Exception {
	final String input = "-- testing module with some export specifications\n" +
			             "module Main ( Blubb(Blabb, Bloob) ) where\n" +
			             "\n" +
			             "import Bla\n" +
			             "\n" +
			             "data Blubb = Blibb | Blabb | Bloob\n";

	IModule module = parseModule(input);
    IExportSpecification[] exports = module.getExportSpecifications();
    assertEquals( 1, exports.length );
    IExportThingWith exportTW = ( IExportThingWith )exports[ 0 ];
    assertEquals( "Blubb", exportTW.getName() );
    
    IExportThingWithComponent[] names = exportTW.getComponents();
    assertEquals( 2, names.length );
    assertEquals( "Blabb", names[ 0 ].getName() );
    assertEquals( "Bloob", names[ 1 ].getName() );
  }
  
  public void testImportWithoutHiding() throws Exception {
    final String input = "module Main where\n" +
    		             "\n" +
    		             "import Bla\n" +
    		             "import Data.List (unzip3, unzip4, unzip5, unzip6, unzip7)";
	IModule module = parseModule(input);
    IImport[] imports = module.getImports();
    assertEquals( 2, imports.length );
    
    IImport imp1 = imports[ 0 ];
    assertEquals( 0, imp1.getImportSpecifications().length );
    assertEquals( "Bla", imp1.getImportedElement() );

    IImport imp2 = imports[ 1 ];
    assertEquals( "Data.List", imp2.getImportedElement() );

    IImportSpecification[] impSpecs = imp2.getImportSpecifications();
    assertEquals( 5, impSpecs.length );
    assertTrue( impSpecs[ 0 ].getImport() == imports[ 1 ] );
    assertEquals( "unzip4", impSpecs[ 1 ].getName() );
    assertEquals( "unzip6", impSpecs[ 3 ].getName() );
    assertFalse( imports[ 1 ].isHiding() );
  }

  public void testImportWithHiding() throws Exception {
	final String input = "module Main where\n" +
			"\n" +
			"import Prelude hiding (max, min)";
	IModule module = parseModule(input);
    IImport[] imports = module.getImports();
    assertEquals( 1, imports.length );

    IImport imp = imports[ 0 ];
    assertEquals( "Prelude", imp.getImportedElement() );

    IImportSpecification[] impSpecs = imp.getImportSpecifications();
    assertEquals( 2, impSpecs.length );
    assertTrue( impSpecs[ 0 ].getImport() == imports[ 0 ] );
    assertEquals( "max", impSpecs[ 0 ].getName() );
    assertEquals( "min", impSpecs[ 1 ].getName() );
    assertTrue( imports[ 0 ].isHiding() );
  }

  public void testClassDecl() throws Exception {
	final String input = "module Main where\n" +
			             "\n" +
			             "class Eq a => Hash a where\n" +
			             "  hash :: a -> Int\n" +
			             "\n" +
			             "class Visible a where\n" +
			             "  toString :: a -> String\n" +
			             "  size :: a -> Int";
    IModule module = parseModule(input);
    IDeclaration[] decls = module.getDeclarations();
    assertEquals( 2, decls.length );
    
    IClassDeclaration classDecl = ( IClassDeclaration )decls[ 0 ];
    assertEquals( "Hash", classDecl.getName() );
    
    IClassDeclaration classDecl2 = ( IClassDeclaration )decls[ 1 ];
    assertEquals( "Visible", classDecl2.getName() );
    ITypeSignature[] signatures = classDecl2.getTypeSignatures();
    assertEquals( 2, signatures.length );
    assertEquals( "toString", signatures[ 0 ].getIdentifiers()[ 0 ] );
    assertEquals( "size", signatures[ 1 ].getIdentifiers()[ 0 ] );
  }

  public void testInstanceDecl() throws Exception {
	final String input = "module Main where\n" +
				         "\n" +
				         "instance Visible Bool where\n" +
				         "  toString True  = \"True\"\n" +
				         "  toString False = \"False\"\n" +
				         "  size _ = 1\n";
    IModule module = parseModule(input);
    IDeclaration[] decls = module.getDeclarations();
    assertEquals( 1, decls.length );

    IInstanceDeclaration decl = ( IInstanceDeclaration )decls[ 0 ];
    assertEquals( "Visible", decl.getName() );
    assertEquals( 2, decl.getSourceLocation().getLine() );
    assertEquals( 0, decl.getSourceLocation().getColumn() );
  }

  public void testDefaultDecl() throws Exception {
	final String input = "module Main where\n" +
			             "\n" +
			             "default (Integer, Double)";
	IModule module = parseModule(input);
    IDeclaration[] decls = module.getDeclarations();
    assertEquals( 1, decls.length );
    IDefaultDeclaration decl = ( IDefaultDeclaration )decls[ 0 ];
    assertEquals( "default declaration", decl.getName() );
    assertEquals( 2, decl.getSourceLocation().getLine() );
    assertEquals( 0, decl.getSourceLocation().getColumn() );
  }

  public void testInfixDecl() throws Exception {
	final String input = "module Main where\n" +
			             "\n" +
			             "infix 5 `op1`\n" +
			             "infixr 1 `op2`\n" +
			             "\n" +
			             "infixl 5 `op2`\n" +
			             "\n" +
			             "infix 5 `op1`, `op2`\n" +
			             "infixl 0 `op1`, +, `opx`";
    IModule module = parseModule(input);
    IDeclaration[] decls = module.getDeclarations();
    assertEquals( 5, decls.length );
    
    IInfixDeclaration decl = ( IInfixDeclaration )decls[ 0 ];
    assertEquals( 5, decl.getPrecedenceLevel() );
    assertEquals( IInfixDeclaration.ASSOC_NONE, decl.getAssociativity() );
    assertEquals( 1, decl.getOperators().length );
    assertEquals( "op1", decl.getOperators()[ 0 ] );
    
    IInfixDeclaration decl1 = ( IInfixDeclaration )decls[ 1 ];
    assertEquals( 1, decl1.getPrecedenceLevel() );
    assertEquals( IInfixDeclaration.ASSOC_RIGHT, decl1.getAssociativity() );
    assertEquals( 1, decl1.getOperators().length );
    assertEquals( "op2", decl1.getOperators()[ 0 ] );
    
    IInfixDeclaration decl2 = ( IInfixDeclaration )decls[ 2 ];
    assertEquals( 5, decl2.getPrecedenceLevel() );
    assertEquals( IInfixDeclaration.ASSOC_LEFT, decl2.getAssociativity() );
    assertEquals( 1, decl2.getOperators().length );
    assertEquals( "op2", decl2.getOperators()[ 0 ] );
    
    IInfixDeclaration decl3 = ( IInfixDeclaration )decls[ 3 ];
    assertEquals( 5, decl3.getPrecedenceLevel() );
    assertEquals( IInfixDeclaration.ASSOC_NONE, decl3.getAssociativity() );
    assertEquals( 2, decl3.getOperators().length );
    assertEquals( "op1", decl3.getOperators()[ 0 ] );
    assertEquals( "op2", decl3.getOperators()[ 1 ] );
    
    IInfixDeclaration decl4 = ( IInfixDeclaration )decls[ 4 ];
    assertEquals( 0, decl4.getPrecedenceLevel() );
    assertEquals( IInfixDeclaration.ASSOC_LEFT, decl4.getAssociativity() );
    assertEquals( 3, decl4.getOperators().length );
    assertEquals( "op1", decl4.getOperators()[ 0 ] );
    assertEquals( "+", decl4.getOperators()[ 1 ] );
    assertEquals( "opx", decl4.getOperators()[ 2 ] );
  }

  public void testTypeDecl() throws Exception {
	final String input = "module Main where\n" +
			             "\n" +
			             "type Rec a = [Circ a]";
	IModule module = parseModule(input);
    assertEquals( 1, module.getDeclarations().length );
    ITypeDeclaration decl = ( ITypeDeclaration )module.getDeclarations()[ 0 ];
    assertEquals( "Rec", decl.getName() );
    assertEquals( 2, decl.getSourceLocation().getLine() );
    assertEquals( 0, decl.getSourceLocation().getColumn() );
  }
  
  public void testNewTypeDecl() throws Exception {
	final String input = "module Main where\n" +
			             "\n" +
			             "newtype Age = Age { unAge :: Int }";
    IModule module = parseModule(input);
    assertEquals( 1, module.getDeclarations().length );
    IDeclaration declaration = module.getDeclarations()[ 0 ];
    INewTypeDeclaration decl = ( INewTypeDeclaration )declaration;
    assertEquals( "Age", decl.getName() );
    assertEquals( 2, decl.getSourceLocation().getLine() );
    assertEquals( 0, decl.getSourceLocation().getColumn() );
  }

  public void testDataDecl() throws Exception {
	final String input = "module Main where\n" +
			             "\n" +
			             "data Eq a => Set a = NilSet | ConsSet a (Set a)\n" +
			             "\n" +
			             "data Temp = Cold | Hot";
    IModule module = parseModule(input);
    assertEquals( 2, module.getDeclarations().length );
    
    IDataDeclaration decl = ( IDataDeclaration )module.getDeclarations()[ 0 ];
    assertEquals( "Set", decl.getName() );
    assertEquals( 2, decl.getSourceLocation().getLine() );
    assertEquals( 0, decl.getSourceLocation().getColumn() );
    assertEquals( 2, decl.getConstructors().length );
    
    IConstructor const0 = decl.getConstructors()[ 0 ];
    assertEquals( "NilSet", const0.getName() );
    assertEquals( 2, const0.getSourceLocation().getLine() );
    assertEquals( 21, const0.getSourceLocation().getColumn() );
    
    IConstructor const1 = decl.getConstructors()[ 1 ];
    assertEquals( "ConsSet", const1.getName() );
    assertEquals( 2, const1.getSourceLocation().getLine() );
    assertEquals( 30, const1.getSourceLocation().getColumn() );
    
    IDataDeclaration decl1 = ( IDataDeclaration )module.getDeclarations()[ 1 ];
    assertEquals( "Temp", decl1.getName() );
    assertEquals( 4, decl1.getSourceLocation().getLine() );
    assertEquals( 0, decl1.getSourceLocation().getColumn() );
    assertEquals( 2, decl1.getConstructors().length );

    IConstructor const2 = decl1.getConstructors()[ 0 ];
    assertEquals( "Cold", const2.getName() );
    assertEquals( 4, const2.getSourceLocation().getLine() );
    assertEquals( 12, const2.getSourceLocation().getColumn() );

    IConstructor const3 = decl1.getConstructors()[ 1 ];
    assertEquals( "Hot", const3.getName() );
    assertEquals( 4, const3.getSourceLocation().getLine() );
    assertEquals( 19, const3.getSourceLocation().getColumn() );
  }

  public void testFunctionBinding() throws Exception {
	final String input = "\n" +
			             "module Main () where\n" +
			             "\n" +
			             "bla :: Bool -> Int -> Int\n" +
			             "bla True 0 = 1\n" +
			             "bla False 0 = 2\n" +
			             "bla _ n = n + 2";
    IModule module = parseModule(input);
    IDeclaration[] decls = module.getDeclarations();
    assertEquals( 2, decls.length );
    
    IFunctionBinding fb1 = ( IFunctionBinding )decls[ 1 ];
    assertEquals( 4, fb1.getSourceLocation().getLine() );
    assertEquals( 0, fb1.getSourceLocation().getColumn() );
    
    assertEquals( 3, fb1.getMatches().length );
    IMatch match = fb1.getMatches()[ 0 ];
    assertEquals( "bla", match.getName() );
    assertEquals( 4, match.getSourceLocation().getLine() );
    assertEquals( 0, match.getSourceLocation().getColumn() );
  }
  
  public void testRealWorldCrash() throws Exception {
	final String input = "module ClassDeclTypeSigLineCrash where\n" +
			             "class A a where\n" +
			             "    dummy :: a -> Int\n" +
			             "    dummy _ = undefined";
	IModule module = parseModule(input);
    IDeclaration[] decls = module.getDeclarations();
    assertEquals( 1, decls.length );
    
    IClassDeclaration classDecl = ( IClassDeclaration )decls[ 0 ];
    assertEquals( "A", classDecl.getName() );
    
    ITypeSignature[] signatures = classDecl.getTypeSignatures();
    assertEquals( 1, signatures.length );
    assertEquals( "dummy", signatures[ 0 ].getIdentifiers()[ 0 ] );
  }
  
  
  // helping methods
  //////////////////
  
  private IModule parseModule(String input) throws CoreException {
    ICompilationUnit cu = parseAsFile(input);
	return cu.getModules()[ 0 ];
  }

}
