package com.parzivail.jade.lexing;

import com.parzivail.jade.lexing.state.StateArrivalHandler;
import com.parzivail.jade.lexing.state.StateMachine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Predicate;

public class Tokenizer extends StateMachine
{
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

	static
	{
		singleCharTokens.put('+', TokenType.Plus);
		singleCharTokens.put('-', TokenType.Minus);
		singleCharTokens.put('*', TokenType.Asterisk);
		singleCharTokens.put('/', TokenType.Slash);
		singleCharTokens.put('\\', TokenType.Backslash);
		singleCharTokens.put('~', TokenType.Tilde);
		singleCharTokens.put('`', TokenType.Grave);
		singleCharTokens.put('!', TokenType.Bang);
		singleCharTokens.put('.', TokenType.Dot);
		singleCharTokens.put(',', TokenType.Comma);
		singleCharTokens.put('?', TokenType.Question);
		singleCharTokens.put('(', TokenType.OpenParen);
		singleCharTokens.put(')', TokenType.CloseParen);
		singleCharTokens.put('[', TokenType.OpenSquare);
		singleCharTokens.put(']', TokenType.CloseSquare);
		singleCharTokens.put('{', TokenType.OpenCurly);
		singleCharTokens.put('}', TokenType.CloseCurly);
		singleCharTokens.put('|', TokenType.Pipe);
		singleCharTokens.put('&', TokenType.Amp);
		singleCharTokens.put('@', TokenType.At);
		singleCharTokens.put('#', TokenType.Pound);
		singleCharTokens.put(';', TokenType.Semicolon);
		singleCharTokens.put(':', TokenType.Colon);
		singleCharTokens.put('\'', TokenType.SingleQuote);
		singleCharTokens.put('"', TokenType.DoubleQuote);
		singleCharTokens.put('$', TokenType.Dollar);
		singleCharTokens.put('^', TokenType.Caret);

		multiCharTokens.put('%', TokenizeState.PercentOrRightRot);
		multiCharTokens.put('>', TokenizeState.GreaterOrRightShift);
		multiCharTokens.put('<', TokenizeState.LessOrLeftShiftRot);
		multiCharTokens.put('=', TokenizeState.AssignOrEquals);
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

	private void throwUnexpectedEof()
	{
		throw new TokenizeException("Unexpected EOF", text.length());
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

		// TODO: relax requirements if already within an Identifier?
		if (Character.isUnicodeIdentifierPart(nextChar))
		{
			moveCharacterToState(TokenizeState.Identifier);
			return;
		}

		emitToken(new IdentifierToken(tokenAccumulator.toString(), getTokenStart()), TokenizeState.EndIfTerminated);
	}

	private void onDigit(TokenType tokenType, TokenizeState possibleNextState, Predicate<Character> predicate)
	{
		if (isEof())
		{
			emitToken(new NumericToken(tokenType, tokenAccumulator.toString(), getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (predicate.test(nextChar))
		{
			moveCharacterToState(possibleNextState);
			return;
		}

		emitToken(new NumericToken(tokenType, tokenAccumulator.toString(), getTokenStart()), TokenizeState.EndIfTerminated);
	}

	@StateArrivalHandler(TokenizeState.HexLiteral)
	private void onHexLiteral()
	{
		onDigit(TokenType.HexLiteral, TokenizeState.HexLiteral, Tokenizer::isHexDigit);
	}

	@StateArrivalHandler(TokenizeState.IntegerLiteral)
	private void onIntegerLiteral()
	{
		onDigit(TokenType.IntegerLiteral, TokenizeState.IntegerLiteral, Tokenizer::isDecimalDigit);
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
			emitToken(new NumericToken(TokenType.IntegerLiteral, tokenAccumulator.toString(), getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		switch (nextChar)
		{
			case 'b' -> resetCharacterToState(TokenizeState.BinaryLiteral);
			case 'c' -> resetCharacterToState(TokenizeState.OctalLiteral);
			case 'x' -> resetCharacterToState(TokenizeState.HexLiteral);
		}

		if (isDecimalDigit(nextChar))
		{
			moveCharacterToState(TokenizeState.IntegerLiteral);
			return;
		}

		throw new TokenizeException("Invalid character in numeric literal", nextChar, cursor);
	}

	@StateArrivalHandler(TokenizeState.EmitEof)
	private void onEmitEof()
	{
		emitToken(new Token(TokenType.Eof, getTokenStart()), TokenizeState.Eof);
	}

	@StateArrivalHandler(TokenizeState.GreaterOrRightShift)
	private void onGreaterOrRightShift()
	{
		if (isEof())
		{
			emitToken(new Token(TokenType.Greater, getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (nextChar == '>')
		{
			popOneTextChar();
			emitToken(new Token(TokenType.RightShift, getTokenStart()), TokenizeState.End);
		}
		else
			emitToken(new Token(TokenType.Greater, getTokenStart()), TokenizeState.End);
	}

	@StateArrivalHandler(TokenizeState.PercentOrRightRot)
	private void onPercentOrRightRot()
	{
		if (isEof())
		{
			emitToken(new Token(TokenType.Percent, getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (nextChar == '>')
		{
			popOneTextChar();
			emitToken(new Token(TokenType.RightRotate, getTokenStart()), TokenizeState.End);
		}
		else
			emitToken(new Token(TokenType.Percent, getTokenStart()), TokenizeState.End);
	}

	@StateArrivalHandler(TokenizeState.LessOrLeftShiftRot)
	private void onLessOrLeftShiftRot()
	{
		if (isEof())
		{
			emitToken(new Token(TokenType.Less, getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (nextChar == '<')
		{
			popOneTextChar();
			emitToken(new Token(TokenType.LeftShift, getTokenStart()), TokenizeState.End);
		}
		else if (nextChar == '%')
		{
			popOneTextChar();
			emitToken(new Token(TokenType.LeftRotate, getTokenStart()), TokenizeState.End);
		}
		else
			emitToken(new Token(TokenType.Less, getTokenStart()), TokenizeState.End);
	}

	@StateArrivalHandler(TokenizeState.AssignOrEquals)
	private void onAssignOrEquals()
	{
		if (isEof())
		{
			emitToken(new Token(TokenType.Assign, getTokenStart()), TokenizeState.EmitEof);
			return;
		}

		var nextChar = text.charAt(0);

		if (nextChar == '=')
		{
			popOneTextChar();
			emitToken(new Token(TokenType.Equals, getTokenStart()), TokenizeState.End);
		}
		else
			emitToken(new Token(TokenType.Assign, getTokenStart()), TokenizeState.End);
	}

	@StateArrivalHandler(TokenizeState.Begin)
	private void onBegin()
	{
		// TODO: greater-equals, less-equals, not-equals, ||, &&

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
				return;
			}

			var multiCharTokenState = multiCharTokens.get(nextChar);
			if (multiCharTokenState != null)
			{
				moveCharacterToState(multiCharTokenState);
				return;
			}

			if (!requireTerminatingToken)
			{
				if (nextChar == '0')
				{
					moveCharacterToState(TokenizeState.NumericLiteral);
					return;
				}

				if (isDecimalDigit(nextChar))
				{
					moveCharacterToState(TokenizeState.IntegerLiteral);
					return;
				}

				if (Character.isUnicodeIdentifierStart(nextChar))
				{
					moveCharacterToState(TokenizeState.Identifier);
					return;
				}
			}

			if (Character.isWhitespace(nextChar))
			{
				requireTerminatingToken = false;
				popOneTextChar();
			}
			else
				throw new TokenizeException(String.format("Unexpected %stoken", requireTerminatingToken ? "non-terminating " : ""), nextChar, cursor);
		}
		while (getState() == TokenizeState.Begin);
	}

	public boolean consume()
	{
		var oldCursor = cursor;

		requireTerminatingToken = getState() == TokenizeState.EndIfTerminated;
		setState(TokenizeState.Begin);

		var newState = getState();
		if (newState != TokenizeState.End && newState != TokenizeState.EndIfTerminated && newState != TokenizeState.Eof)
			throw new TokenizeException("Failed to consume token", oldCursor);

		return newState != TokenizeState.Eof;
	}
}
