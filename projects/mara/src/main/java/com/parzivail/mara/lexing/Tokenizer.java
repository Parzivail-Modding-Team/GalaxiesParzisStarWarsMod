package com.parzivail.mara.lexing;

import com.parzivail.mara.lexing.state.StateArrivalHandler;
import com.parzivail.mara.lexing.state.StateMachine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Predicate;

public class Tokenizer extends StateMachine
{
	private boolean isIdentifierStartChar(char c)
	{
		return c == '_' || Character.isUnicodeIdentifierStart(c);
	}

	private static boolean isHexDigit(char c)
	{
		return c >= '0' && c <= '9' ||
		       (c >= 'A' && c <= 'F') ||
		       (c >= 'a' && c <= 'f');
	}

	private static boolean isDecimalDigit(char c)
	{
		return c >= '0' && c <= '9';
	}

	private static boolean isOctalDigit(char c)
	{
		return c >= '0' && c <= '7';
	}

	private static boolean isBinaryDigit(char c)
	{
		return c == '0' || c == '1';
	}

	private static final HashMap<Character, TokenType> singleCharTokens = new HashMap<>();
	private static final HashMap<Character, TokenizeState> multiCharTokens = new HashMap<>();
	private static final HashMap<Character, TokenizeState> transparentStateTokens = new HashMap<>();

	static
	{
		singleCharTokens.put('+', TokenType.Plus);
		singleCharTokens.put('-', TokenType.Minus);
		singleCharTokens.put('*', TokenType.Asterisk);
		singleCharTokens.put('\\', TokenType.Backslash);
		singleCharTokens.put('~', TokenType.Tilde);
		singleCharTokens.put('`', TokenType.Grave);
		singleCharTokens.put(',', TokenType.Comma);
		singleCharTokens.put('?', TokenType.Question);
		singleCharTokens.put('(', TokenType.OpenParen);
		singleCharTokens.put(')', TokenType.CloseParen);
		singleCharTokens.put('[', TokenType.OpenSquare);
		singleCharTokens.put(']', TokenType.CloseSquare);
		singleCharTokens.put('{', TokenType.OpenCurly);
		singleCharTokens.put('}', TokenType.CloseCurly);
		singleCharTokens.put('@', TokenType.At);
		singleCharTokens.put('#', TokenType.Pound);
		singleCharTokens.put(';', TokenType.Semicolon);
		singleCharTokens.put(':', TokenType.Colon);
		singleCharTokens.put('$', TokenType.Dollar);
		singleCharTokens.put('^', TokenType.Caret);

		multiCharTokens.put('%', TokenizeState.PercentOrRightRot);
		multiCharTokens.put('>', TokenizeState.GreaterOrRightShiftOrGreaterEquals);
		multiCharTokens.put('<', TokenizeState.LessOrLeftShiftOrLeftRotOrLessEquals);
		multiCharTokens.put('=', TokenizeState.AssignOrEqualsOrArrow);
		multiCharTokens.put('|', TokenizeState.PipeOrBooleanOr);
		multiCharTokens.put('&', TokenizeState.AmpOrBooleanAnd);
		multiCharTokens.put('!', TokenizeState.BangOrNotEquals);
		multiCharTokens.put('.', TokenizeState.DotOrFloatingPointLiteral);
		multiCharTokens.put('/', TokenizeState.SlashOrComment);

		transparentStateTokens.put('\'', TokenizeState.CharacterLiteral);
		transparentStateTokens.put('"', TokenizeState.StringLiteral);
	}

	private final StringBuilder tokenAccumulator = new StringBuilder();

	private String text;
	private int cursor = 0;
	private boolean requireTerminatingToken;

	private LinkedList<Token> tokens = new LinkedList<>();

	public Tokenizer(String text)
	{
		super(TokenizeState.Begin);
		this.text = text;
	}

	public LinkedList<Token> getTokens()
	{
		return tokens;
	}

	private int getTokenStart()
	{
		return cursor - tokenAccumulator.length();
	}

	private void popOneTextChar()
	{
		text = text.substring(1);
		cursor++;
	}

	private void emitOneCharToken(TokenType type)
	{
		popOneTextChar();
		emitToken(new Token(type, cursor - 1), TokenizeState.End);
	}

	private void moveCharacterToState(TokenizeState next)
	{
		tokenAccumulator.append(text.charAt(0));
		popOneTextChar();
		setState(next);
	}

	private void resetCharacterToState(TokenizeState next)
	{
		tokenAccumulator.setLength(0);
		popOneTextChar();
		setState(next);
	}

	private void emitToken(Token token, TokenizeState state)
	{
		tokens.addLast(token);
		tokenAccumulator.setLength(0);
		setState(state);
	}

	private boolean isEof()
	{
		return text.isEmpty();
	}

	private void onDigit(TokenType tokenType, TokenizeState possibleNextState, Predicate<Character> predicate)
	{
		if (isEof())
		{
			emitToken(new NumericToken(tokenType, tokenAccumulator.toString(), NumericToken.BASES.get(tokenType), getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (predicate.test(nextChar))
		{
			moveCharacterToState(possibleNextState);
			return;
		}

		if (isDecimalDigit(nextChar))
			throw new TokenizeException("Invalid digit in literal", cursor);

		emitToken(new NumericToken(tokenType, tokenAccumulator.toString(), NumericToken.BASES.get(tokenType), getTokenStart()), TokenizeState.EndIfTerminated);
	}

	private void onTwoCharacterToken(TokenType oneCharType, TokenPair... pairs)
	{
		if (isEof())
		{
			emitToken(new Token(oneCharType, getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		for (var pair : pairs)
		{
			if (nextChar == pair.character())
			{
				popOneTextChar();
				emitToken(new Token(pair.result(), getTokenStart()), TokenizeState.End);
				return;
			}
		}

		emitToken(new Token(oneCharType, getTokenStart()), TokenizeState.End);
	}

	@StateArrivalHandler(TokenizeState.StringUnicodeEscape)
	private void onStringUnicodeEscape()
	{
		if (isEof())
			throw new TokenizeException("Unexpected EOF in unicode string escape", cursor);

		var sb = new StringBuilder(4);
		for (var i = 0; i < 4; i++)
		{
			var nextChar = text.charAt(0);

			if (!isHexDigit(nextChar))
				throw new TokenizeException("Unexpected character in unicode string escape", cursor);

			popOneTextChar();
			sb.append(nextChar);
		}

		tokenAccumulator.append(new String(new int[] { Integer.parseInt(sb.toString(), 16) }, 0, 1));
	}

	@StateArrivalHandler(TokenizeState.StringEscape)
	private void onStringEscape()
	{
		if (isEof())
			throw new TokenizeException("Unexpected EOF in string escape", cursor);

		var nextChar = text.charAt(0);
		popOneTextChar();

		switch (nextChar)
		{
			case 'b' -> tokenAccumulator.append('\b');
			case 't' -> tokenAccumulator.append('\t');
			case 'n' -> tokenAccumulator.append('\n');
			case 'r' -> tokenAccumulator.append('\r');
			case 'f' -> tokenAccumulator.append('\f');
			case '\\' -> tokenAccumulator.append('\\');
			case '\"' -> tokenAccumulator.append('"');
			case '\'' -> tokenAccumulator.append('\'');
			case 'u' -> setState(TokenizeState.StringUnicodeEscape);
		}
	}

	@StateArrivalHandler(TokenizeState.StringLiteral)
	private void onStringLiteral()
	{
		if (isEof())
			throw new TokenizeException("Unexpected EOF in string", cursor);

		char nextChar;
		do
		{
			nextChar = text.charAt(0);
			popOneTextChar();

			if (nextChar == '\\')
				setState(TokenizeState.StringEscape);
			else if (nextChar != '"')
				tokenAccumulator.append(nextChar);
		}
		while (nextChar != '"');

		emitToken(new StringToken(tokenAccumulator.toString(), getTokenStart()), TokenizeState.End);
	}

	@StateArrivalHandler(TokenizeState.CharacterLiteral)
	private void onCharacterLiteral()
	{
		if (isEof())
			throw new TokenizeException("Unexpected EOF in character literal", cursor);

		var nextChar = text.charAt(0);
		popOneTextChar();

		if (nextChar == '\\')
			setState(TokenizeState.StringEscape);
		else
			tokenAccumulator.append(nextChar);

		nextChar = text.charAt(0);
		if (nextChar != '\'')
			throw new TokenizeException("Multiple characters in character literal", cursor);

		popOneTextChar();

		emitToken(new CharacterToken(tokenAccumulator.charAt(0), getTokenStart()), TokenizeState.End);
	}

	@StateArrivalHandler(TokenizeState.Identifier)
	private void onIdentifier()
	{
		if (isEof())
		{
			emitToken(new IdentifierToken(tokenAccumulator.toString(), getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (Character.isUnicodeIdentifierPart(nextChar))
		{
			moveCharacterToState(TokenizeState.Identifier);
			return;
		}

		emitToken(new IdentifierToken(tokenAccumulator.toString(), getTokenStart()), TokenizeState.EndIfTerminated);
	}

	@StateArrivalHandler(TokenizeState.HexLiteral)
	private void onHexLiteral()
	{
		onDigit(TokenType.HexLiteral, TokenizeState.HexLiteral, Tokenizer::isHexDigit);
	}

	@StateArrivalHandler(TokenizeState.FloatingPointLiteral)
	private void onFloatingPointLiteral()
	{
		if (isEof())
		{
			emitToken(new FloatingPointToken(tokenAccumulator.toString(), getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (isDecimalDigit(nextChar))
		{
			moveCharacterToState(TokenizeState.FloatingPointLiteral);
			return;
		}

		emitToken(new FloatingPointToken(tokenAccumulator.toString(), getTokenStart()), TokenizeState.EndIfTerminated);
	}

	@StateArrivalHandler(TokenizeState.DecimalLiteral)
	private void onIntegerLiteral()
	{
		if (isEof())
		{
			emitToken(new NumericToken(TokenType.DecimalLiteral, tokenAccumulator.toString(), 10, getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (isDecimalDigit(nextChar))
		{
			moveCharacterToState(TokenizeState.DecimalLiteral);
			return;
		}
		else if (nextChar == '.')
		{
			moveCharacterToState(TokenizeState.FloatingPointLiteral);
			return;
		}

		emitToken(new NumericToken(TokenType.DecimalLiteral, tokenAccumulator.toString(), 10, getTokenStart()), TokenizeState.EndIfTerminated);
	}

	@StateArrivalHandler(TokenizeState.OctalLiteral)
	private void onOctalLiteral()
	{
		onDigit(TokenType.OctalLiteral, TokenizeState.OctalLiteral, Tokenizer::isOctalDigit);
	}

	@StateArrivalHandler(TokenizeState.BinaryLiteral)
	private void onBinaryLiteral()
	{
		onDigit(TokenType.BinaryLiteral, TokenizeState.BinaryLiteral, Tokenizer::isBinaryDigit);
	}

	@StateArrivalHandler(TokenizeState.NumericLiteral)
	private void onDigit()
	{
		if (isEof())
		{
			emitToken(new NumericToken(TokenType.DecimalLiteral, tokenAccumulator.toString(), 10, getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		switch (nextChar)
		{
			case 'b':
				resetCharacterToState(TokenizeState.BinaryLiteral);
				return;
			case 'c':
				resetCharacterToState(TokenizeState.OctalLiteral);
				return;
			case 'x':
				resetCharacterToState(TokenizeState.HexLiteral);
				return;
			case '.':
				moveCharacterToState(TokenizeState.FloatingPointLiteral);
				return;
		}

		if (isDecimalDigit(nextChar))
		{
			moveCharacterToState(TokenizeState.DecimalLiteral);
			return;
		}

		emitToken(new NumericToken(TokenType.DecimalLiteral, tokenAccumulator.toString(), 10, getTokenStart()), TokenizeState.EndIfTerminated);
	}

	@StateArrivalHandler(TokenizeState.DotOrFloatingPointLiteral)
	private void onDotOrFloatingPointLiteral()
	{
		if (isEof())
		{
			emitToken(new Token(TokenType.Dot, getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (isDecimalDigit(nextChar))
		{
			moveCharacterToState(TokenizeState.FloatingPointLiteral);
			return;
		}

		emitToken(new Token(TokenType.Dot, getTokenStart()), TokenizeState.End);
	}

	@StateArrivalHandler(TokenizeState.GreaterOrRightShiftOrGreaterEquals)
	private void onGreaterOrRightShiftOrGreaterEquals()
	{
		onTwoCharacterToken(
				TokenType.Greater,
				new TokenPair('>', TokenType.RightShift),
				new TokenPair('=', TokenType.GreaterEquals)
		);
	}

	@StateArrivalHandler(TokenizeState.PercentOrRightRot)
	private void onPercentOrRightRot()
	{
		onTwoCharacterToken(
				TokenType.Percent,
				new TokenPair('>', TokenType.RightRotate)
		);
	}

	@StateArrivalHandler(TokenizeState.LessOrLeftShiftOrLeftRotOrLessEquals)
	private void onLessOrLeftShiftOrLeftRotOrLessEquals()
	{
		onTwoCharacterToken(
				TokenType.Less,
				new TokenPair('<', TokenType.LeftShift),
				new TokenPair('%', TokenType.LeftRotate),
				new TokenPair('=', TokenType.LessEquals)
		);
	}

	@StateArrivalHandler(TokenizeState.AssignOrEqualsOrArrow)
	private void onAssignOrEqualsOrArrow()
	{
		onTwoCharacterToken(
				TokenType.Assign,
				new TokenPair('=', TokenType.Equals),
				new TokenPair('>', TokenType.RightArrow)
		);
	}

	@StateArrivalHandler(TokenizeState.PipeOrBooleanOr)
	private void onPipeOrBooleanOr()
	{
		onTwoCharacterToken(
				TokenType.Pipe,
				new TokenPair('|', TokenType.Or)
		);
	}

	@StateArrivalHandler(TokenizeState.AmpOrBooleanAnd)
	private void onAmpOrBooleanAnd()
	{
		onTwoCharacterToken(
				TokenType.Amp,
				new TokenPair('&', TokenType.And)
		);
	}

	@StateArrivalHandler(TokenizeState.BangOrNotEquals)
	private void onBangOrNotEquals()
	{
		onTwoCharacterToken(
				TokenType.Bang,
				new TokenPair('=', TokenType.NotEquals)
		);
	}

	@StateArrivalHandler(TokenizeState.SlashOrComment)
	private void onSlashOrComment()
	{
		if (isEof())
		{
			emitToken(new Token(TokenType.Slash, getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (nextChar == '/')
		{
			resetCharacterToState(TokenizeState.LineComment);
			return;
		}

		emitToken(new Token(TokenType.Slash, getTokenStart()), TokenizeState.End);
	}

	@StateArrivalHandler(TokenizeState.LineComment)
	private void onLineComment()
	{
		char c;

		// Consume all characters until a line break
		do
		{
			c = text.charAt(0);
			popOneTextChar();
		}
		while (c != '\n');

		// Ignore this token and begin to consume another
		restart();
	}

	@StateArrivalHandler(TokenizeState.Begin)
	private void onBegin()
	{
		// TODO: comments, floating point scientific notation

		do
		{
			if (isEof())
			{
				setState(TokenizeState.EmitEof);
				return;
			}

			var nextChar = text.charAt(0);

			var singleCharTokenType = singleCharTokens.get(nextChar);
			if (singleCharTokenType != null)
			{
				emitOneCharToken(singleCharTokenType);
				continue;
			}

			var multiCharTokenState = multiCharTokens.get(nextChar);
			if (multiCharTokenState != null)
			{
				moveCharacterToState(multiCharTokenState);
				continue;
			}

			var transparentStateTokensState = transparentStateTokens.get(nextChar);
			if (transparentStateTokensState != null)
			{
				popOneTextChar();
				setState(transparentStateTokensState);
				continue;
			}

			if (!requireTerminatingToken)
			{
				if (nextChar == '0')
				{
					moveCharacterToState(TokenizeState.NumericLiteral);
					continue;
				}

				if (isDecimalDigit(nextChar))
				{
					moveCharacterToState(TokenizeState.DecimalLiteral);
					continue;
				}
			}

			if (isIdentifierStartChar(nextChar))
			{
				moveCharacterToState(TokenizeState.Identifier);
				continue;
			}

			if (Character.isWhitespace(nextChar))
			{
				requireTerminatingToken = false;
				popOneTextChar();
				continue;
			}

			throw new TokenizeException(String.format("Unexpected %stoken", requireTerminatingToken ? "non-terminating " : ""), text.charAt(0), cursor);
		}
		while (getState() == TokenizeState.Begin);
	}

	private Token getLastToken()
	{
		return tokens.isEmpty() ? null : tokens.getLast();
	}

	public boolean consume()
	{
		var oldCursor = cursor;
		var oldState = getState();
		var oldLastToken = getLastToken();

		if (oldState == TokenizeState.EmitEof)
		{
			emitToken(new Token(TokenType.Eof, getTokenStart()), TokenizeState.Eof);
			return false;
		}

		requireTerminatingToken = oldState == TokenizeState.EndIfTerminated;
		setState(TokenizeState.Begin);

		var newState = getState();

		if (getLastToken() == oldLastToken && newState == TokenizeState.EmitEof)
		{
			emitToken(new Token(TokenType.Eof, getTokenStart()), TokenizeState.Eof);
			return false;
		}

		if (newState != TokenizeState.End && newState != TokenizeState.EndIfTerminated && newState != TokenizeState.EmitEof)
			throw new TokenizeException("Failed to consume token", oldCursor);

		return true;
	}
}
