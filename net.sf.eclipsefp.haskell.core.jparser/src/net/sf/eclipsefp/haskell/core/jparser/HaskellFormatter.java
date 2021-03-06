package net.sf.eclipsefp.haskell.core.jparser;

import java.util.Stack;

import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;

public class HaskellFormatter extends TokenStreamProcessor {

	private final Stack<Integer> fLayoutContextStack = new Stack<Integer>();
	
	public HaskellFormatter(final TokenStream in) {
		super(new PreprocessedTokenStream(
				new HaskellCommentFilter(
					new QualifiedIdentifierFilter(in))));
	}

	@Override
  protected void insertTokensAsNeeded() throws TokenStreamException {
		if (hasInsertedTokens())
			return;
		
		if (isOpenBlock(peekToken())) {
			Token openBlockToken = peekToken();
			
			if (!fLayoutContextStack.isEmpty() && openBlockToken.getColumn() > fLayoutContextStack.peek()) {
				consumeToken();
				insertToken(new EclipseFPToken(HaskellLexerTokenTypes.LEFT_CURLY));
				fLayoutContextStack.push(openBlockToken.getColumn());
			} else if (fLayoutContextStack.isEmpty() && openBlockToken.getColumn() > -1) {
				consumeToken();
				insertToken(new EclipseFPToken(HaskellLexerTokenTypes.LEFT_CURLY));
				fLayoutContextStack.push(openBlockToken.getColumn());
			} else {
				openBlockToken.setType(HaskellLexerExtendedTokenTypes.LINEBREAK);
				insertToken(new EclipseFPToken(HaskellLexerTokenTypes.LEFT_CURLY));
				insertToken(new EclipseFPToken(HaskellLexerTokenTypes.RIGHT_CURLY));
			}
			
		} else if (isLineBreak(peekToken())) {
			Token lineBreakToken = peekToken();
			
			if (!fLayoutContextStack.isEmpty() && lineBreakToken.getColumn() == fLayoutContextStack.peek()) {
				consumeToken();
				insertToken(new EclipseFPToken(HaskellLexerTokenTypes.SEMICOLON));
			} else if (!fLayoutContextStack.isEmpty() && lineBreakToken.getColumn() < fLayoutContextStack.peek()) {
				insertToken(new EclipseFPToken(HaskellLexerTokenTypes.RIGHT_CURLY));
				fLayoutContextStack.pop();
			} else {
				consumeToken();
			}
		} else if (isEof(peekToken())) {
			if (!fLayoutContextStack.isEmpty() && fLayoutContextStack.peek() != -1) {
				insertToken(new EclipseFPToken(HaskellLexerTokenTypes.RIGHT_CURLY));
				fLayoutContextStack.pop();
			}
		}
		
	}

	private boolean isLineBreak(final Token token) {
		return token.getType() == HaskellLexerExtendedTokenTypes.LINEBREAK;
	}

	private boolean isOpenBlock(final Token token) {
		return token.getType() == HaskellLexerExtendedTokenTypes.OPENBLOCK;
	}

	private boolean isEof(final Token token) {
		return token.getType() == HaskellLexerTokenTypes.EOF;
	}

}
