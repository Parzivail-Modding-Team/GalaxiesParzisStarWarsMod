package com.parzivail.mara;

import com.parzivail.mara.lexing.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LexerTests
{
	private Token assertToken(Tokenizer tokenizer, TokenType type)
	{
		Assertions.assertTrue(tokenizer.consume());

		var token = tokenizer.getTokens().getLast();

		Assertions.assertNotNull(token);
		Assertions.assertEquals(type, token.type);

		return token;
	}

	private void assertEof(Tokenizer tokenizer)
	{
		Assertions.assertFalse(tokenizer.consume());

		var token = tokenizer.getTokens().getLast();

		Assertions.assertNotNull(token);
		Assertions.assertEquals(TokenType.Eof, token.type);
	}

	private void assertIdentifier(Tokenizer tokenizer, String value)
	{
		var token = assertToken(tokenizer, TokenType.Identifier);
		Assertions.assertInstanceOf(IdentifierToken.class, token);
		Assertions.assertEquals(value, ((IdentifierToken)token).value);
	}

	private void assertString(Tokenizer tokenizer, String value)
	{
		var token = assertToken(tokenizer, TokenType.StringLiteral);
		Assertions.assertInstanceOf(StringToken.class, token);
		Assertions.assertEquals(value, ((StringToken)token).value);
	}

	private void assertInt(Tokenizer tokenizer, int value)
	{
		var token = assertToken(tokenizer, TokenType.IntegerLiteral);
		Assertions.assertInstanceOf(NumericToken.class, token);
		Assertions.assertEquals(value, ((NumericToken)token).uintValue());
	}

	private void assertInvalid(Tokenizer tokenizer)
	{
		Assertions.assertThrows(TokenizeException.class, tokenizer::consume);
	}

	@Test
	public void emptyEof(TestInfo testInfo) throws Exception
	{
		assertEof(new Tokenizer(""));
	}

	@Test
	public void invalidId0(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("0test");
		assertInvalid(tokenizer);
	}

	@Test
	public void validId0(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("_test");
		assertIdentifier(tokenizer, "_test");
		assertEof(tokenizer);
	}

	@Test
	public void validId1(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("test0");
		assertIdentifier(tokenizer, "test0");
		assertEof(tokenizer);
	}

	@Test
	public void identifierPlus(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("test0 +");
		assertIdentifier(tokenizer, "test0");
		assertToken(tokenizer, TokenType.Plus);
		assertEof(tokenizer);
	}

	@Test
	public void identifierPlusTrailingWhitespace(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("test0 + ");
		assertIdentifier(tokenizer, "test0");
		assertToken(tokenizer, TokenType.Plus);
		assertEof(tokenizer);
	}

	@Test
	public void equalsAssign(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("= == ===");
		assertToken(tokenizer, TokenType.Assign);
		assertToken(tokenizer, TokenType.Equals);
		assertToken(tokenizer, TokenType.Equals);
		assertToken(tokenizer, TokenType.Assign);
		assertEof(tokenizer);
	}

	@Test
	public void identIntIdent(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("a123 456b");
		assertIdentifier(tokenizer, "a123");
		assertInt(tokenizer, 456);
		assertInvalid(tokenizer);
	}

	@Test
	public void identIntIdent2(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("a123 456 b");
		assertIdentifier(tokenizer, "a123");
		assertInt(tokenizer, 456);
		assertIdentifier(tokenizer, "b");
		assertEof(tokenizer);
	}

	@Test
	public void stringLiteral(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("\"hello, world!\"");
		assertString(tokenizer, "hello, world!");
		assertEof(tokenizer);
	}

	@Test
	public void stringLiteralEscape0(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("\"hello,\nworld!\"");
		assertString(tokenizer, "hello,\nworld!");
		assertEof(tokenizer);
	}

	@Test
	public void stringLiteralEscape1(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("\"hello, world!\n\"");
		assertString(tokenizer, "hello, world!\n");
		assertEof(tokenizer);
	}
}
