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

	private void assertInt(Tokenizer tokenizer, String value, TokenType type)
	{
		var token = assertToken(tokenizer, type);
		Assertions.assertInstanceOf(NumericToken.class, token);
		Assertions.assertEquals(value, ((NumericToken)token).value);
		Assertions.assertEquals(NumericToken.BASES.get(type), ((NumericToken)token).base);
	}

	private void assertFloat(Tokenizer tokenizer, String value)
	{
		var token = assertToken(tokenizer, TokenType.FloatingPointLiteral);
		Assertions.assertInstanceOf(FloatingPointToken.class, token);
		Assertions.assertEquals(value, ((FloatingPointToken)token).value);
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
	public void integerBases(TestInfo testInfo) throws Exception
	{
		Assertions.assertEquals(16, NumericToken.BASES.get(TokenType.HexLiteral));
		Assertions.assertEquals(10, NumericToken.BASES.get(TokenType.DecimalLiteral));
		Assertions.assertEquals(8, NumericToken.BASES.get(TokenType.OctalLiteral));
		Assertions.assertEquals(2, NumericToken.BASES.get(TokenType.BinaryLiteral));
	}

	@Test
	public void intIdentifier(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("0test");
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "test");
		assertEof(tokenizer);
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
		assertInt(tokenizer, "456", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "b");
		assertEof(tokenizer);
	}

	@Test
	public void identIntIdent2(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("a123 456 b");
		assertIdentifier(tokenizer, "a123");
		assertInt(tokenizer, "456", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "b");
		assertEof(tokenizer);
	}

	@Test
	public void identIntIdent3(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("a123b456c");
		assertIdentifier(tokenizer, "a123b456c");
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

	@Test
	public void stringLiteralEscape2(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("\"\\\"hello\\\", world!\n\"");
		assertString(tokenizer, "\"hello\", world!\n");
		assertEof(tokenizer);
	}

	@Test
	public void nbt0(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("{Item:{id:\"minecraft:diamond_sword\",Count:1b,tag:{Damage:10}}}");
		assertToken(tokenizer, TokenType.OpenCurly);
		assertIdentifier(tokenizer, "Item");
		assertToken(tokenizer, TokenType.Colon);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertIdentifier(tokenizer, "id");
		assertToken(tokenizer, TokenType.Colon);
		assertString(tokenizer, "minecraft:diamond_sword");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "Count");
		assertToken(tokenizer, TokenType.Colon);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "b");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "tag");
		assertToken(tokenizer, TokenType.Colon);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertIdentifier(tokenizer, "Damage");
		assertToken(tokenizer, TokenType.Colon);
		assertInt(tokenizer, "10", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertEof(tokenizer);
	}

	@Test
	public void blockstate0(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("pswg:block[facing=east,half=top,light=4]");
		assertIdentifier(tokenizer, "pswg");
		assertToken(tokenizer, TokenType.Colon);
		assertIdentifier(tokenizer, "block");
		assertToken(tokenizer, TokenType.OpenSquare);
		assertIdentifier(tokenizer, "facing");
		assertToken(tokenizer, TokenType.Assign);
		assertIdentifier(tokenizer, "east");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "half");
		assertToken(tokenizer, TokenType.Assign);
		assertIdentifier(tokenizer, "top");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "light");
		assertToken(tokenizer, TokenType.Assign);
		assertInt(tokenizer, "4", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseSquare);
		assertEof(tokenizer);
	}

	@Test
	public void integers0(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("0 01 0123 123456 9999999999999999999999999999 0x0 0x00 0x000 0x123 0x012 0b101101 0b00000 0c0 0c1234567 000000");
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertInt(tokenizer, "01", TokenType.DecimalLiteral);
		assertInt(tokenizer, "0123", TokenType.DecimalLiteral);
		assertInt(tokenizer, "123456", TokenType.DecimalLiteral);
		assertInt(tokenizer, "9999999999999999999999999999", TokenType.DecimalLiteral);
		assertInt(tokenizer, "0", TokenType.HexLiteral);
		assertInt(tokenizer, "00", TokenType.HexLiteral);
		assertInt(tokenizer, "000", TokenType.HexLiteral);
		assertInt(tokenizer, "123", TokenType.HexLiteral);
		assertInt(tokenizer, "012", TokenType.HexLiteral);
		assertInt(tokenizer, "101101", TokenType.BinaryLiteral);
		assertInt(tokenizer, "00000", TokenType.BinaryLiteral);
		assertInt(tokenizer, "0", TokenType.OctalLiteral);
		assertInt(tokenizer, "1234567", TokenType.OctalLiteral);
		assertInt(tokenizer, "000000", TokenType.DecimalLiteral);
		assertEof(tokenizer);
	}

	@Test
	public void floatingPoint0(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("0 123 123.456 1.234 1.234f 1.234d 0.123 -0.123 .234 -.234 123. 0.0 123");
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertInt(tokenizer, "123", TokenType.DecimalLiteral);
		assertFloat(tokenizer, "123.456");
		assertFloat(tokenizer, "1.234");
		assertFloat(tokenizer, "1.234");
		assertIdentifier(tokenizer, "f");
		assertFloat(tokenizer, "1.234");
		assertIdentifier(tokenizer, "d");
		assertFloat(tokenizer, "0.123");
		assertToken(tokenizer, TokenType.Minus);
		assertFloat(tokenizer, "0.123");
		assertFloat(tokenizer, ".234");
		assertToken(tokenizer, TokenType.Minus);
		assertFloat(tokenizer, ".234");
		assertFloat(tokenizer, "123.");
		assertFloat(tokenizer, "0.0");
		assertInt(tokenizer, "123", TokenType.DecimalLiteral);
		assertEof(tokenizer);
	}

	@Test
	public void postfixArrayIndexInt(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("array[0x10]");
		assertIdentifier(tokenizer, "array");
		assertToken(tokenizer, TokenType.OpenSquare);
		assertInt(tokenizer, "10", TokenType.HexLiteral);
		assertToken(tokenizer, TokenType.CloseSquare);
		assertEof(tokenizer);
	}

	@Test
	public void postfixArrayIndexAddition(TestInfo testInfo) throws Exception
	{
		var tokenizer = new Tokenizer("array[offset + 0b11010110]");
		assertIdentifier(tokenizer, "array");
		assertToken(tokenizer, TokenType.OpenSquare);
		assertIdentifier(tokenizer, "offset");
		assertToken(tokenizer, TokenType.Plus);
		assertInt(tokenizer, "11010110", TokenType.BinaryLiteral);
		assertToken(tokenizer, TokenType.CloseSquare);
		assertEof(tokenizer);
	}
}
