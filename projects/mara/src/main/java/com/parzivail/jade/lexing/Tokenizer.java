package com.parzivail.jade.lexing;

import com.parzivail.jade.lexing.state.StateArrivalHandler;
import com.parzivail.jade.lexing.state.StateMachine;

import java.util.LinkedList;
import java.util.function.Predicate;

public class Tokenizer extends StateMachine
{
	private static boolean isValidIdentifierCharacter(char c)
	{
		return (c >= 'A' && c <= 'Z') ||
		       (c >= 'a' && c <= 'z') ||
		       c == '_';
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
		if (isValidIdentifierCharacter(nextChar))
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
		else if(nextChar == '%')
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

			// TODO: this would be a great HashMap
			switch (nextChar)
			{
				case '+':
					emitOneCharToken(TokenType.Plus);
					return;
				case '-':
					emitOneCharToken(TokenType.Minus);
					return;
				case '*':
					emitOneCharToken(TokenType.Asterisk);
					return;
				case '/':
					emitOneCharToken(TokenType.Slash);
					return;
				case '\\':
					emitOneCharToken(TokenType.Backslash);
					return;
				case '~':
					emitOneCharToken(TokenType.Tilde);
					return;
				case '`':
					emitOneCharToken(TokenType.Grave);
					return;
				case '!':
					emitOneCharToken(TokenType.Bang);
					return;
				case '.':
					emitOneCharToken(TokenType.Dot);
					return;
				case ',':
					emitOneCharToken(TokenType.Comma);
					return;
				case '?':
					emitOneCharToken(TokenType.Question);
					return;
				case '(':
					emitOneCharToken(TokenType.OpenParen);
					return;
				case ')':
					emitOneCharToken(TokenType.CloseParen);
					return;
				case '[':
					emitOneCharToken(TokenType.OpenSquare);
					return;
				case ']':
					emitOneCharToken(TokenType.CloseSquare);
					return;
				case '{':
					emitOneCharToken(TokenType.OpenCurly);
					return;
				case '}':
					emitOneCharToken(TokenType.CloseCurly);
					return;
				case '|':
					emitOneCharToken(TokenType.Pipe);
					return;
				case '&':
					emitOneCharToken(TokenType.Amp);
					return;
				case '@':
					emitOneCharToken(TokenType.At);
					return;
				case '#':
					emitOneCharToken(TokenType.Pound);
					return;
				case ';':
					emitOneCharToken(TokenType.Semicolon);
					return;
				case ':':
					emitOneCharToken(TokenType.Colon);
					return;
				case '\'':
					emitOneCharToken(TokenType.SingleQuote);
					return;
				case '"':
					emitOneCharToken(TokenType.DoubleQuote);
					return;
				case '$':
					emitOneCharToken(TokenType.Dollar);
					return;
				case '^':
					emitOneCharToken(TokenType.Caret);
					return;
				case '%':
					moveCharacterToState(TokenizeState.PercentOrRightRot);
					return;
				case '>':
					moveCharacterToState(TokenizeState.GreaterOrRightShift);
					return;
				case '<':
					moveCharacterToState(TokenizeState.LessOrLeftShiftRot);
					return;
				case '=':
					moveCharacterToState(TokenizeState.AssignOrEquals);
					return;
				case '0':
					if (!requireTerminatingToken)
					{
						moveCharacterToState(TokenizeState.NumericLiteral);
						return;
					}
			}

			if (isDecimalDigit(nextChar) && !requireTerminatingToken)
			{
				moveCharacterToState(TokenizeState.IntegerLiteral);
				return;
			}

			if (isValidIdentifierCharacter(nextChar) && !requireTerminatingToken)
			{
				moveCharacterToState(TokenizeState.Identifier);
				return;
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
