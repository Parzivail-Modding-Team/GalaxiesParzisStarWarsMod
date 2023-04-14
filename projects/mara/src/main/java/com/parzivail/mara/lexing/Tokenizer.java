package com.parzivail.mara.lexing;

import com.parzivail.mara.lexing.state.StateArrivalHandler;
import com.parzivail.mara.lexing.state.StateMachine;
import com.parzivail.mara.lexing.token.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Predicate;

public class Tokenizer extends StateMachine
{
	private boolean isIdentifierStart(char c)
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

	private static boolean isFloatingPointExponentStart(char c)
	{
		return c == 'e' || c == 'E';
	}

	private static final HashMap<String, TokenType> keywords = new HashMap<>();
	private static final HashMap<Character, TokenType> singleCharTokens = new HashMap<>();
	private static final HashMap<Character, TokenizeState> multiCharTokens = new HashMap<>();
	private static final HashMap<Character, TokenizeState> transparentStateTokens = new HashMap<>();

	static
	{
		keywords.put("true", TokenType.KwTrue);
		keywords.put("false", TokenType.KwFalse);
		keywords.put("var", TokenType.KwVar);
		keywords.put("if", TokenType.KwIf);
		keywords.put("else", TokenType.KwElse);
		keywords.put("for", TokenType.KwFor);
		keywords.put("using", TokenType.KwUsing);
		keywords.put("return", TokenType.KwReturn);

		singleCharTokens.put('*', TokenType.Asterisk);
		singleCharTokens.put('\\', TokenType.Backslash);
		singleCharTokens.put('~', TokenType.Tilde);
		singleCharTokens.put('`', TokenType.Grave);
		singleCharTokens.put(',', TokenType.Comma);
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

		multiCharTokens.put('+', TokenizeState.Plus);
		multiCharTokens.put('-', TokenizeState.Minus);
		multiCharTokens.put('%', TokenizeState.Percent);
		multiCharTokens.put('>', TokenizeState.Greater);
		multiCharTokens.put('<', TokenizeState.Less);
		multiCharTokens.put('=', TokenizeState.Assign);
		multiCharTokens.put('|', TokenizeState.Pipe);
		multiCharTokens.put('?', TokenizeState.Question);
		multiCharTokens.put('&', TokenizeState.Amp);
		multiCharTokens.put('!', TokenizeState.Bang);
		multiCharTokens.put('.', TokenizeState.DotOrFloatingPointLiteral);
		multiCharTokens.put('/', TokenizeState.Slash);

		transparentStateTokens.put('\'', TokenizeState.CharacterLiteral);
		transparentStateTokens.put('"', TokenizeState.StringLiteral);
	}

	private final StringBuilder tokenAccumulator = new StringBuilder();

	private String text;
	private int cursor = 0;
	private boolean requireTerminatingToken;

	private final LinkedList<Token> tokens = new LinkedList<>();

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

	private void emitIdentifier(String value, int location, TokenizeState state)
	{
		var keyword = keywords.get(value);
		emitToken(keyword != null ? new KeywordToken(keyword, location) : new IdentifierToken(value, location), state);
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
			emitIdentifier(tokenAccumulator.toString(), getTokenStart(), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (Character.isUnicodeIdentifierPart(nextChar))
		{
			moveCharacterToState(TokenizeState.Identifier);
			return;
		}

		emitIdentifier(tokenAccumulator.toString(), getTokenStart(), TokenizeState.EndIfTerminated);
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

		if (isFloatingPointExponentStart(nextChar))
		{
			moveCharacterToState(TokenizeState.FloatingPointExponentStart);
			return;
		}

		emitToken(new FloatingPointToken(tokenAccumulator.toString(), getTokenStart()), TokenizeState.EndIfTerminated);
	}

	@StateArrivalHandler(TokenizeState.FloatingPointExponentLiteral)
	private void onFloatingPointExponentLiteral()
	{
		if (isEof())
		{
			emitToken(new FloatingPointToken(tokenAccumulator.toString(), getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (isDecimalDigit(nextChar))
		{
			moveCharacterToState(TokenizeState.FloatingPointExponentLiteral);
			return;
		}

		emitToken(new FloatingPointToken(tokenAccumulator.toString(), getTokenStart()), TokenizeState.EndIfTerminated);
	}

	@StateArrivalHandler(TokenizeState.FloatingPointExponentStart)
	private void onFloatingPointExponentStart()
	{
		if (isEof())
			throw new TokenizeException("Unexpected EOF in floating point exponential", cursor);

		var nextChar = text.charAt(0);

		if (nextChar == '+' || nextChar == '-' || isDecimalDigit(nextChar))
		{
			moveCharacterToState(TokenizeState.FloatingPointExponentLiteral);
			return;
		}

		throw new TokenizeException("Unexpected character in floating point exponential", cursor);
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
		else if (isFloatingPointExponentStart(nextChar))
		{
			moveCharacterToState(TokenizeState.FloatingPointExponentStart);
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

	@StateArrivalHandler(TokenizeState.Greater)
	private void onGreater()
	{
		onTwoCharacterToken(
				TokenType.Greater,
				new TokenPair('>', TokenType.RightShift),
				new TokenPair('=', TokenType.GreaterEquals)
		);
	}

	@StateArrivalHandler(TokenizeState.Percent)
	private void onPercent()
	{
		onTwoCharacterToken(
				TokenType.Percent,
				new TokenPair('>', TokenType.RightRotate)
		);
	}

	@StateArrivalHandler(TokenizeState.Less)
	private void onLess()
	{
		onTwoCharacterToken(
				TokenType.Less,
				new TokenPair('<', TokenType.LeftShift),
				new TokenPair('%', TokenType.LeftRotate),
				new TokenPair('=', TokenType.LessEquals)
		);
	}

	@StateArrivalHandler(TokenizeState.Assign)
	private void onAssign()
	{
		onTwoCharacterToken(
				TokenType.Assign,
				new TokenPair('=', TokenType.Equals),
				new TokenPair('>', TokenType.RightArrow)
		);
	}

	@StateArrivalHandler(TokenizeState.Pipe)
	private void onPipe()
	{
		onTwoCharacterToken(
				TokenType.Pipe,
				new TokenPair('|', TokenType.Or)
		);
	}

	@StateArrivalHandler(TokenizeState.Question)
	private void onQuestion()
	{
		onTwoCharacterToken(
				TokenType.Question,
				new TokenPair('?', TokenType.Coalesce)
		);
	}

	@StateArrivalHandler(TokenizeState.Amp)
	private void onAmp()
	{
		onTwoCharacterToken(
				TokenType.Amp,
				new TokenPair('&', TokenType.And)
		);
	}

	@StateArrivalHandler(TokenizeState.Bang)
	private void onBang()
	{
		onTwoCharacterToken(
				TokenType.Bang,
				new TokenPair('=', TokenType.NotEquals)
		);
	}

	@StateArrivalHandler(TokenizeState.Plus)
	private void onPlus()
	{
		onTwoCharacterToken(
				TokenType.Plus,
				new TokenPair('+', TokenType.Increment)
		);
	}

	@StateArrivalHandler(TokenizeState.Minus)
	private void onMinus()
	{
		onTwoCharacterToken(
				TokenType.Minus,
				new TokenPair('-', TokenType.Decrement)
		);
	}

	@StateArrivalHandler(TokenizeState.Slash)
	private void onSlash()
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

			if (isIdentifierStart(nextChar))
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

	public boolean consume()
	{
		var oldCursor = cursor;
		var oldState = getState();
		var oldLastToken = tokens.peekLast();

		if (oldState == TokenizeState.EmitEof)
		{
			emitToken(new Token(TokenType.Eof, getTokenStart()), TokenizeState.Eof);
			return false;
		}

		requireTerminatingToken = oldState == TokenizeState.EndIfTerminated;
		setState(TokenizeState.Begin);

		var newState = getState();

		if (tokens.peekLast() == oldLastToken && newState == TokenizeState.EmitEof)
		{
			emitToken(new Token(TokenType.Eof, getTokenStart()), TokenizeState.Eof);
			return false;
		}

		if (newState != TokenizeState.End && newState != TokenizeState.EndIfTerminated && newState != TokenizeState.EmitEof)
			throw new TokenizeException("Failed to consume token", oldCursor);

		return true;
	}

	public void consumeAll()
	{
		while (consume())
			;
	}
}
