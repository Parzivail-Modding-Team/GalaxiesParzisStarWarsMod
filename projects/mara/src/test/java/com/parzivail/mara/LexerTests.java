package com.parzivail.mara;

import com.parzivail.mara.lexing.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LexerTests
{
	private static Token assertToken(Tokenizer tokenizer, TokenType type)
	{
		Assertions.assertTrue(tokenizer.consume());

		var token = tokenizer.getTokens().getLast();

		Assertions.assertNotNull(token);
		Assertions.assertEquals(type, token.type);

		return token;
	}

	private static void assertEof(Tokenizer tokenizer)
	{
		Assertions.assertFalse(tokenizer.consume());

		var token = tokenizer.getTokens().getLast();

		Assertions.assertNotNull(token);
		Assertions.assertEquals(TokenType.Eof, token.type);
	}

	private static void assertKeyword(Tokenizer tokenizer, TokenType value)
	{
		var token = assertToken(tokenizer, value);
		Assertions.assertInstanceOf(KeywordToken.class, token);
	}

	private static void assertIdentifier(Tokenizer tokenizer, String value)
	{
		var token = assertToken(tokenizer, TokenType.Identifier);
		Assertions.assertInstanceOf(IdentifierToken.class, token);
		Assertions.assertEquals(value, ((IdentifierToken)token).value);
	}

	private static void assertString(Tokenizer tokenizer, String value)
	{
		var token = assertToken(tokenizer, TokenType.StringLiteral);
		Assertions.assertInstanceOf(StringToken.class, token);
		Assertions.assertEquals(value, ((StringToken)token).value);
	}

	private static void assertCharacter(Tokenizer tokenizer, char value)
	{
		var token = assertToken(tokenizer, TokenType.CharacterLiteral);
		Assertions.assertInstanceOf(CharacterToken.class, token);
		Assertions.assertEquals(value, ((CharacterToken)token).value);
	}

	private static void assertInt(Tokenizer tokenizer, String value, TokenType type)
	{
		var token = assertToken(tokenizer, type);
		Assertions.assertInstanceOf(NumericToken.class, token);
		Assertions.assertEquals(value, ((NumericToken)token).value);
		Assertions.assertEquals(NumericToken.BASES.get(type), ((NumericToken)token).base);
	}

	private static void assertFloat(Tokenizer tokenizer, String value)
	{
		var token = assertToken(tokenizer, TokenType.FloatingPointLiteral);
		Assertions.assertInstanceOf(FloatingPointToken.class, token);
		Assertions.assertEquals(value, ((FloatingPointToken)token).value);
	}

	private static void assertInvalid(Tokenizer tokenizer)
	{
		Assertions.assertThrows(TokenizeException.class, tokenizer::consume);
	}

	@Test
	public void emptyEof(TestInfo testInfo)
	{
		assertEof(new Tokenizer(""));
	}

	@Test
	public void integerBases(TestInfo testInfo)
	{
		Assertions.assertEquals(16, NumericToken.BASES.get(TokenType.HexLiteral));
		Assertions.assertEquals(10, NumericToken.BASES.get(TokenType.DecimalLiteral));
		Assertions.assertEquals(8, NumericToken.BASES.get(TokenType.OctalLiteral));
		Assertions.assertEquals(2, NumericToken.BASES.get(TokenType.BinaryLiteral));
	}

	@Test
	public void intIdentifier(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("0test");
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "test");
		assertEof(tokenizer);
	}

	@Test
	public void validId0(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("_test");
		assertIdentifier(tokenizer, "_test");
		assertEof(tokenizer);
	}

	@Test
	public void validId1(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("test0");
		assertIdentifier(tokenizer, "test0");
		assertEof(tokenizer);
	}

	@Test
	public void identifierPlus(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("test0 +");
		assertIdentifier(tokenizer, "test0");
		assertToken(tokenizer, TokenType.Plus);
		assertEof(tokenizer);
	}

	@Test
	public void identifierPlusTrailingWhitespace(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("test0 + ");
		assertIdentifier(tokenizer, "test0");
		assertToken(tokenizer, TokenType.Plus);
		assertEof(tokenizer);
	}

	@Test
	public void equalsAssign(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("= == ===");
		assertToken(tokenizer, TokenType.Assign);
		assertToken(tokenizer, TokenType.Equals);
		assertToken(tokenizer, TokenType.Equals);
		assertToken(tokenizer, TokenType.Assign);
		assertEof(tokenizer);
	}

	@Test
	public void questionCoalesce(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("? ?? ???");
		assertToken(tokenizer, TokenType.Question);
		assertToken(tokenizer, TokenType.Coalesce);
		assertToken(tokenizer, TokenType.Coalesce);
		assertToken(tokenizer, TokenType.Question);
		assertEof(tokenizer);
	}

	@Test
	public void ampAnd(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("& && &&&");
		assertToken(tokenizer, TokenType.Amp);
		assertToken(tokenizer, TokenType.And);
		assertToken(tokenizer, TokenType.And);
		assertToken(tokenizer, TokenType.Amp);
		assertEof(tokenizer);
	}

	@Test
	public void pipeOr(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("| || |||");
		assertToken(tokenizer, TokenType.Pipe);
		assertToken(tokenizer, TokenType.Or);
		assertToken(tokenizer, TokenType.Or);
		assertToken(tokenizer, TokenType.Pipe);
		assertEof(tokenizer);
	}

	@Test
	public void lessLeftShift(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("< << <<<");
		assertToken(tokenizer, TokenType.Less);
		assertToken(tokenizer, TokenType.LeftShift);
		assertToken(tokenizer, TokenType.LeftShift);
		assertToken(tokenizer, TokenType.Less);
		assertEof(tokenizer);
	}

	@Test
	public void greaterRightShift(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("> >> >>>");
		assertToken(tokenizer, TokenType.Greater);
		assertToken(tokenizer, TokenType.RightShift);
		assertToken(tokenizer, TokenType.RightShift);
		assertToken(tokenizer, TokenType.Greater);
		assertEof(tokenizer);
	}

	@Test
	public void rightRotate(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(">> %>");
		assertToken(tokenizer, TokenType.RightShift);
		assertToken(tokenizer, TokenType.RightRotate);
		assertEof(tokenizer);
	}

	@Test
	public void leftRotate(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("<< <%");
		assertToken(tokenizer, TokenType.LeftShift);
		assertToken(tokenizer, TokenType.LeftRotate);
		assertEof(tokenizer);
	}

	@Test
	public void plusIncrement(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("+ ++ +++");
		assertToken(tokenizer, TokenType.Plus);
		assertToken(tokenizer, TokenType.Increment);
		assertToken(tokenizer, TokenType.Increment);
		assertToken(tokenizer, TokenType.Plus);
		assertEof(tokenizer);
	}

	@Test
	public void minusDecrement(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("- -- ---");
		assertToken(tokenizer, TokenType.Minus);
		assertToken(tokenizer, TokenType.Decrement);
		assertToken(tokenizer, TokenType.Decrement);
		assertToken(tokenizer, TokenType.Minus);
		assertEof(tokenizer);
	}

	@Test
	public void identIntIdent(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("a123 456b");
		assertIdentifier(tokenizer, "a123");
		assertInt(tokenizer, "456", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "b");
		assertEof(tokenizer);
	}

	@Test
	public void identIntIdent2(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("a123 456 b");
		assertIdentifier(tokenizer, "a123");
		assertInt(tokenizer, "456", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "b");
		assertEof(tokenizer);
	}

	@Test
	public void identIntIdent3(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("a123b456c");
		assertIdentifier(tokenizer, "a123b456c");
		assertEof(tokenizer);
	}

	@Test
	public void stringLiteral(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("\"hello, world!\"");
		assertString(tokenizer, "hello, world!");
		assertEof(tokenizer);
	}

	@Test
	public void stringLiteralEscape0(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("\"hello,\nworld!\"");
		assertString(tokenizer, "hello,\nworld!");
		assertEof(tokenizer);
	}

	@Test
	public void stringLiteralEscape1(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("\"hello, world!\n\"");
		assertString(tokenizer, "hello, world!\n");
		assertEof(tokenizer);
	}

	@Test
	public void stringLiteralEscape2(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("\"\\\"hello\\\", world!\n\"");
		assertString(tokenizer, "\"hello\", world!\n");
		assertEof(tokenizer);
	}

	@Test
	public void stringLiteralEscape3(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("\"hello,\\u0020DEADBEEF\\u0020world!\"");
		assertString(tokenizer, "hello, DEADBEEF world!");
		assertEof(tokenizer);
	}

	@Test
	public void nbt0(TestInfo testInfo)
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
	public void nbt1(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("diamond_pickaxe{display:{Name:\"{\\\"text\\\":\\\"Aw, Man\\\",\\\"color\\\":\\\"gold\\\"}\",Lore:[\"{\\\"text\\\":\\\"So we back in the mines...\\\",\\\"color\\\":\\\"gray\\\",\\\"italic\\\":true}\"]},Damage:1,Enchantments:[{id:\"minecraft:efficiency\",lvl:5s},{id:\"minecraft:unbreaking\",lvl:3s}]}");
		assertIdentifier(tokenizer, "diamond_pickaxe");
		assertToken(tokenizer, TokenType.OpenCurly);
		assertIdentifier(tokenizer, "display");
		assertToken(tokenizer, TokenType.Colon);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertIdentifier(tokenizer, "Name");
		assertToken(tokenizer, TokenType.Colon);
		assertString(tokenizer, "{\"text\":\"Aw, Man\",\"color\":\"gold\"}");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "Lore");
		assertToken(tokenizer, TokenType.Colon);
		assertToken(tokenizer, TokenType.OpenSquare);
		assertString(tokenizer, "{\"text\":\"So we back in the mines...\",\"color\":\"gray\",\"italic\":true}");
		assertToken(tokenizer, TokenType.CloseSquare);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "Damage");
		assertToken(tokenizer, TokenType.Colon);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "Enchantments");
		assertToken(tokenizer, TokenType.Colon);
		assertToken(tokenizer, TokenType.OpenSquare);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertIdentifier(tokenizer, "id");
		assertToken(tokenizer, TokenType.Colon);
		assertString(tokenizer, "minecraft:efficiency");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "lvl");
		assertToken(tokenizer, TokenType.Colon);
		assertInt(tokenizer, "5", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "s");
		assertToken(tokenizer, TokenType.CloseCurly);
		assertToken(tokenizer, TokenType.Comma);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertIdentifier(tokenizer, "id");
		assertToken(tokenizer, TokenType.Colon);
		assertString(tokenizer, "minecraft:unbreaking");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "lvl");
		assertToken(tokenizer, TokenType.Colon);
		assertInt(tokenizer, "3", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "s");
		assertToken(tokenizer, TokenType.CloseCurly);
		assertToken(tokenizer, TokenType.CloseSquare);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertEof(tokenizer);
	}

	@Test
	public void blockstate0(TestInfo testInfo)
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
	public void integers0(TestInfo testInfo)
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
	public void invalidOctal(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("0c12348");
		assertInvalid(tokenizer);
	}

	@Test
	public void invalidBinary(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("0b103");
		assertInvalid(tokenizer);
	}

	@Test
	public void floatingPoint0(TestInfo testInfo)
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
	public void floatingPoint1(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("0 0.0 .0 0. 1 1.01 .1 1. 1e0 1e-1 1e+2 1.0e-30 .345e+345");
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertFloat(tokenizer, "0.0");
		assertFloat(tokenizer, ".0");
		assertFloat(tokenizer, "0.");
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertFloat(tokenizer, "1.01");
		assertFloat(tokenizer, ".1");
		assertFloat(tokenizer, "1.");
		assertFloat(tokenizer, "1e0");
		assertFloat(tokenizer, "1e-1");
		assertFloat(tokenizer, "1e+2");
		assertFloat(tokenizer, "1.0e-30");
		assertFloat(tokenizer, ".345e+345");
		assertEof(tokenizer);
	}

	@Test
	public void postfixArrayIndexInt(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("array[0x10]");
		assertIdentifier(tokenizer, "array");
		assertToken(tokenizer, TokenType.OpenSquare);
		assertInt(tokenizer, "10", TokenType.HexLiteral);
		assertToken(tokenizer, TokenType.CloseSquare);
		assertEof(tokenizer);
	}

	@Test
	public void postfixArrayIndexAddition(TestInfo testInfo)
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

	@Test
	public void characters0(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("'a' 'b' '\t' '\n' '\\'' '\\\\' '\\u0000' '\\u0020' '\\uDEAD' '\\uBEEF'");
		assertCharacter(tokenizer, 'a');
		assertCharacter(tokenizer, 'b');
		assertCharacter(tokenizer, '\t');
		assertCharacter(tokenizer, '\n');
		assertCharacter(tokenizer, '\'');
		assertCharacter(tokenizer, '\\');
		assertCharacter(tokenizer, '\u0000');
		assertCharacter(tokenizer, '\u0020');
		assertCharacter(tokenizer, '\uDEAD');
		assertCharacter(tokenizer, '\uBEEF');
		assertEof(tokenizer);
	}

	@Test
	public void invalidCharacter0(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("'ab'");
		assertInvalid(tokenizer);
	}

	@Test
	public void invalidCharacter1(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("'\\u000'");
		assertInvalid(tokenizer);
	}

	@Test
	public void invalidCharacter2(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("'\\uBEET'");
		assertInvalid(tokenizer);
	}

	@Test
	public void invalidCharacter3(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer("'\\uCAFE5'");
		assertInvalid(tokenizer);
	}

	@Test
	public void comments0(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						int i = 0;
						// This is a comment
						str s = "hello, world!";
						// Example: s.Length
						print(s);
						"""
		);
		assertIdentifier(tokenizer, "int");
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Assign);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "str");
		assertIdentifier(tokenizer, "s");
		assertToken(tokenizer, TokenType.Assign);
		assertString(tokenizer, "hello, world!");
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "print");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "s");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertEof(tokenizer);
	}

	@Test
	public void blasterConfig(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						new BlasterDescriptor(Resources.id("bowcaster"), BlasterArchetype.HEAVY)
							.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC), BlasterWaterBehavior.BOLTS_PASS_THROUGH_WATER)
							.mechanicalProperties(6.7f, -0.5f, 10, 300)
							.damage(20, 188f, Falloff.cliff(2))
							.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1.25f, 1)
							.recoil(new BlasterAxialInfo(1.5f, 3))
							.spread(new BlasterAxialInfo(0, 0))
							.heat(new BlasterHeatInfo(1008, 100, 12, 20, 14, 100, 80))
							.cooling(new BlasterCoolingBypassProfile(0.5f, 0.05f, 0.3f, 0.06f))
							.attachments(b => b.attachment(1, "repeater", BlasterAttachmentFunction.ALLOW_BURST, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG))
						""");
		assertIdentifier(tokenizer, "new");
		assertIdentifier(tokenizer, "BlasterDescriptor");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "Resources");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "id");
		assertToken(tokenizer, TokenType.OpenParen);
		assertString(tokenizer, "bowcaster");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "BlasterArchetype");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "HEAVY");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "firingBehavior");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "List");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "of");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "BlasterFiringMode");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "SEMI_AUTOMATIC");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "BlasterWaterBehavior");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "BOLTS_PASS_THROUGH_WATER");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "mechanicalProperties");
		assertToken(tokenizer, TokenType.OpenParen);
		assertFloat(tokenizer, "6.7");
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.Comma);
		assertToken(tokenizer, TokenType.Minus);
		assertFloat(tokenizer, "0.5");
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "10", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "300", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "damage");
		assertToken(tokenizer, TokenType.OpenParen);
		assertInt(tokenizer, "20", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "188", TokenType.DecimalLiteral);
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "Falloff");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "cliff");
		assertToken(tokenizer, TokenType.OpenParen);
		assertInt(tokenizer, "2", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "bolt");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "ColorUtil");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "packHsv");
		assertToken(tokenizer, TokenType.OpenParen);
		assertFloat(tokenizer, "0.98");
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Comma);
		assertFloat(tokenizer, "1.25");
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "recoil");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "new");
		assertIdentifier(tokenizer, "BlasterAxialInfo");
		assertToken(tokenizer, TokenType.OpenParen);
		assertFloat(tokenizer, "1.5");
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "3", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "spread");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "new");
		assertIdentifier(tokenizer, "BlasterAxialInfo");
		assertToken(tokenizer, TokenType.OpenParen);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "heat");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "new");
		assertIdentifier(tokenizer, "BlasterHeatInfo");
		assertToken(tokenizer, TokenType.OpenParen);
		assertInt(tokenizer, "1008", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "100", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "12", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "20", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "14", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "100", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "80", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "cooling");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "new");
		assertIdentifier(tokenizer, "BlasterCoolingBypassProfile");
		assertToken(tokenizer, TokenType.OpenParen);
		assertFloat(tokenizer, "0.5");
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.Comma);
		assertFloat(tokenizer, "0.05");
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.Comma);
		assertFloat(tokenizer, "0.3");
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.Comma);
		assertFloat(tokenizer, "0.06");
		assertIdentifier(tokenizer, "f");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "attachments");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "b");
		assertToken(tokenizer, TokenType.RightArrow);
		assertIdentifier(tokenizer, "b");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "attachment");
		assertToken(tokenizer, TokenType.OpenParen);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertString(tokenizer, "repeater");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "BlasterAttachmentFunction");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "ALLOW_BURST");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "BlasterAttachmentCategory");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "INTERNAL_ORDNANCE_CONFIG");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertEof(tokenizer);
	}

	@Test
	public void itemClass(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						public class TcwItems
						{
							@RegistryOrder(0)
							public static class Armor
							{
								@RegistryName("phase1_clonetrooper")
								public static final ArmorItems Phase1Clone = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
								@RegistryName("phase2_clonetrooper")
								public static final ArmorItems Phase2Clone = new ArmorItems(ArmorMaterials.DIAMOND, new Item.Settings().maxCount(1));
							}
						}
						""");
		assertIdentifier(tokenizer, "public");
		assertIdentifier(tokenizer, "class");
		assertIdentifier(tokenizer, "TcwItems");
		assertToken(tokenizer, TokenType.OpenCurly);
		assertToken(tokenizer, TokenType.At);
		assertIdentifier(tokenizer, "RegistryOrder");
		assertToken(tokenizer, TokenType.OpenParen);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "public");
		assertIdentifier(tokenizer, "static");
		assertIdentifier(tokenizer, "class");
		assertIdentifier(tokenizer, "Armor");
		assertToken(tokenizer, TokenType.OpenCurly);
		assertToken(tokenizer, TokenType.At);
		assertIdentifier(tokenizer, "RegistryName");
		assertToken(tokenizer, TokenType.OpenParen);
		assertString(tokenizer, "phase1_clonetrooper");
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "public");
		assertIdentifier(tokenizer, "static");
		assertIdentifier(tokenizer, "final");
		assertIdentifier(tokenizer, "ArmorItems");
		assertIdentifier(tokenizer, "Phase1Clone");
		assertToken(tokenizer, TokenType.Assign);
		assertIdentifier(tokenizer, "new");
		assertIdentifier(tokenizer, "ArmorItems");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "ArmorMaterials");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "DIAMOND");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "new");
		assertIdentifier(tokenizer, "Item");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "Settings");
		assertToken(tokenizer, TokenType.OpenParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "maxCount");
		assertToken(tokenizer, TokenType.OpenParen);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertToken(tokenizer, TokenType.At);
		assertIdentifier(tokenizer, "RegistryName");
		assertToken(tokenizer, TokenType.OpenParen);
		assertString(tokenizer, "phase2_clonetrooper");
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "public");
		assertIdentifier(tokenizer, "static");
		assertIdentifier(tokenizer, "final");
		assertIdentifier(tokenizer, "ArmorItems");
		assertIdentifier(tokenizer, "Phase2Clone");
		assertToken(tokenizer, TokenType.Assign);
		assertIdentifier(tokenizer, "new");
		assertIdentifier(tokenizer, "ArmorItems");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "ArmorMaterials");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "DIAMOND");
		assertToken(tokenizer, TokenType.Comma);
		assertIdentifier(tokenizer, "new");
		assertIdentifier(tokenizer, "Item");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "Settings");
		assertToken(tokenizer, TokenType.OpenParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "maxCount");
		assertToken(tokenizer, TokenType.OpenParen);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertEof(tokenizer);
	}

	@Test
	public void javaMethod(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						private static boolean isHexDigit(char c)
						{
							return c >= '0' && c <= '9' ||
									(c >= 'A' && c <= 'F') ||
									(c >= 'a' && c <= 'f');
						}
						""");
		assertIdentifier(tokenizer, "private");
		assertIdentifier(tokenizer, "static");
		assertIdentifier(tokenizer, "boolean");
		assertIdentifier(tokenizer, "isHexDigit");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "char");
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertKeyword(tokenizer, TokenType.KwReturn);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.GreaterEquals);
		assertCharacter(tokenizer, '0');
		assertToken(tokenizer, TokenType.And);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.LessEquals);
		assertCharacter(tokenizer, '9');
		assertToken(tokenizer, TokenType.Or);
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.GreaterEquals);
		assertCharacter(tokenizer, 'A');
		assertToken(tokenizer, TokenType.And);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.LessEquals);
		assertCharacter(tokenizer, 'F');
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Or);
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.GreaterEquals);
		assertCharacter(tokenizer, 'a');
		assertToken(tokenizer, TokenType.And);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.LessEquals);
		assertCharacter(tokenizer, 'f');
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertEof(tokenizer);
	}

	@Test
	public void javaMethodWithComments(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						// Determine if a given character is a
						// valid hexadecimal digit or not
						private static boolean isHexDigit(char c)
						{
							return c >= '0' && c <= '9' || // Capture digits
									(c >= 'A' && c <= 'F') || // Capture uppercase hex-specific digits
									(c >= 'a' && c <= 'f'); // Capture lowercase hex-specific digits
						}
						""");
		assertIdentifier(tokenizer, "private");
		assertIdentifier(tokenizer, "static");
		assertIdentifier(tokenizer, "boolean");
		assertIdentifier(tokenizer, "isHexDigit");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "char");
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertKeyword(tokenizer, TokenType.KwReturn);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.GreaterEquals);
		assertCharacter(tokenizer, '0');
		assertToken(tokenizer, TokenType.And);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.LessEquals);
		assertCharacter(tokenizer, '9');
		assertToken(tokenizer, TokenType.Or);
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.GreaterEquals);
		assertCharacter(tokenizer, 'A');
		assertToken(tokenizer, TokenType.And);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.LessEquals);
		assertCharacter(tokenizer, 'F');
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Or);
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.GreaterEquals);
		assertCharacter(tokenizer, 'a');
		assertToken(tokenizer, TokenType.And);
		assertIdentifier(tokenizer, "c");
		assertToken(tokenizer, TokenType.LessEquals);
		assertCharacter(tokenizer, 'f');
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertEof(tokenizer);
	}

	@Test
	public void jsonObject(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						{
							"blaster.fire.dc17": {
								"sounds": [
									"pswg_addon_clonewars:blaster/fire/dc17"
								]
							}
						}
						""");
		assertToken(tokenizer, TokenType.OpenCurly);
		assertString(tokenizer, "blaster.fire.dc17");
		assertToken(tokenizer, TokenType.Colon);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertString(tokenizer, "sounds");
		assertToken(tokenizer, TokenType.Colon);
		assertToken(tokenizer, TokenType.OpenSquare);
		assertString(tokenizer, "pswg_addon_clonewars:blaster/fire/dc17");
		assertToken(tokenizer, TokenType.CloseSquare);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertEof(tokenizer);
	}

	@Test
	public void pythonScript(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						from picamera2 import Picamera2
						from picamera2.encoders import Encoder
												
						size = (2592, 1944)
						picam2 = Picamera2()
						video_config = picam2.create_video_configuration(raw={"format": "SGBRG10", "size": size})
						picam2.configure(video_config)
						picam2.encode_stream_name = "raw"
						encoder = Encoder()
						""");
		assertIdentifier(tokenizer, "from");
		assertIdentifier(tokenizer, "picamera2");
		assertIdentifier(tokenizer, "import");
		assertIdentifier(tokenizer, "Picamera2");
		assertIdentifier(tokenizer, "from");
		assertIdentifier(tokenizer, "picamera2");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "encoders");
		assertIdentifier(tokenizer, "import");
		assertIdentifier(tokenizer, "Encoder");
		assertIdentifier(tokenizer, "size");
		assertToken(tokenizer, TokenType.Assign);
		assertToken(tokenizer, TokenType.OpenParen);
		assertInt(tokenizer, "2592", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Comma);
		assertInt(tokenizer, "1944", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "picam2");
		assertToken(tokenizer, TokenType.Assign);
		assertIdentifier(tokenizer, "Picamera2");
		assertToken(tokenizer, TokenType.OpenParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "video_config");
		assertToken(tokenizer, TokenType.Assign);
		assertIdentifier(tokenizer, "picam2");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "create_video_configuration");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "raw");
		assertToken(tokenizer, TokenType.Assign);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertString(tokenizer, "format");
		assertToken(tokenizer, TokenType.Colon);
		assertString(tokenizer, "SGBRG10");
		assertToken(tokenizer, TokenType.Comma);
		assertString(tokenizer, "size");
		assertToken(tokenizer, TokenType.Colon);
		assertIdentifier(tokenizer, "size");
		assertToken(tokenizer, TokenType.CloseCurly);
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "picam2");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "configure");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "video_config");
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "picam2");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "encode_stream_name");
		assertToken(tokenizer, TokenType.Assign);
		assertString(tokenizer, "raw");
		assertIdentifier(tokenizer, "encoder");
		assertToken(tokenizer, TokenType.Assign);
		assertIdentifier(tokenizer, "Encoder");
		assertToken(tokenizer, TokenType.OpenParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertEof(tokenizer);
	}

	@Test
	public void maraBasic(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						using mara;

						// Main entrance point
						int main(str[] args)
						{
							// Print a string using class mara.console
							console.println("hello, world!");

							// Tight loop to print the numbers 0-9
							for (var i = 0; i < 10; i++)
								console.println("number: %s".format(i)); // String formatting method utilized

							// Calculate some return value
							int j = 2;
							j *= 2;
							j++;

							// Sanity check
							console.println(j);

							// Return to the OS
							return j;
						}
						""");
		assertKeyword(tokenizer, TokenType.KwUsing);
		assertIdentifier(tokenizer, "mara");
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "int");
		assertIdentifier(tokenizer, "main");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "str");
		assertToken(tokenizer, TokenType.OpenSquare);
		assertToken(tokenizer, TokenType.CloseSquare);
		assertIdentifier(tokenizer, "args");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertIdentifier(tokenizer, "console");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "println");
		assertToken(tokenizer, TokenType.OpenParen);
		assertString(tokenizer, "hello, world!");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertKeyword(tokenizer, TokenType.KwFor);
		assertToken(tokenizer, TokenType.OpenParen);
		assertKeyword(tokenizer, TokenType.KwVar);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Assign);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Less);
		assertInt(tokenizer, "10", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Increment);
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "console");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "println");
		assertToken(tokenizer, TokenType.OpenParen);
		assertString(tokenizer, "number: %s");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "format");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "int");
		assertIdentifier(tokenizer, "j");
		assertToken(tokenizer, TokenType.Assign);
		assertInt(tokenizer, "2", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "j");
		assertToken(tokenizer, TokenType.Asterisk);
		assertToken(tokenizer, TokenType.Assign);
		assertInt(tokenizer, "2", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "j");
		assertToken(tokenizer, TokenType.Increment);
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "console");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "println");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "j");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertKeyword(tokenizer, TokenType.KwReturn);
		assertIdentifier(tokenizer, "j");
		assertToken(tokenizer, TokenType.Semicolon);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertEof(tokenizer);
	}

	@Test
	public void maraFib(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						int fib(int n)
						{
							if (n < 2)
								return n;

							return fib(n - 1) + fib(n - 2);
						}
						""");
		assertIdentifier(tokenizer, "int");
		assertIdentifier(tokenizer, "fib");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "int");
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertKeyword(tokenizer, TokenType.KwIf);
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.Less);
		assertInt(tokenizer, "2", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertKeyword(tokenizer, TokenType.KwReturn);
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.Semicolon);
		assertKeyword(tokenizer, TokenType.KwReturn);
		assertIdentifier(tokenizer, "fib");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.Minus);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Plus);
		assertIdentifier(tokenizer, "fib");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.Minus);
		assertInt(tokenizer, "2", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertEof(tokenizer);
	}

	@Test
	public void maraFibExpression(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						int fib(int n) => n < 2 ? n : (fib(n - 1) + fib(n - 2));
						""");
		assertIdentifier(tokenizer, "int");
		assertIdentifier(tokenizer, "fib");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "int");
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.RightArrow);
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.Less);
		assertInt(tokenizer, "2", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Question);
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.Colon);
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "fib");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.Minus);
		assertInt(tokenizer, "1", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Plus);
		assertIdentifier(tokenizer, "fib");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.Minus);
		assertInt(tokenizer, "2", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertEof(tokenizer);
	}

	@Test
	public void maraFizzBuzz(TestInfo testInfo)
	{
		var tokenizer = new Tokenizer(
				"""
						using mara;

						fizzbuzz(int n)
						{
							if (i % 3 == 0 && i % 5 == 0)
								console.println("FizzBuzz");
							else if (i % 3 == 0)
								console.println("Fizz");
							else if (i % 5 == 0)
								console.println("Buzz");
							else
								console.println(i);
						}

						main()
						{
							for (var i = 0; i < 100; i++)
								fizzbuzz(i);
						}
						""");
		assertKeyword(tokenizer, TokenType.KwUsing);
		assertIdentifier(tokenizer, "mara");
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "fizzbuzz");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "int");
		assertIdentifier(tokenizer, "n");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertKeyword(tokenizer, TokenType.KwIf);
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Percent);
		assertInt(tokenizer, "3", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Equals);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.And);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Percent);
		assertInt(tokenizer, "5", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Equals);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "console");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "println");
		assertToken(tokenizer, TokenType.OpenParen);
		assertString(tokenizer, "FizzBuzz");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertKeyword(tokenizer, TokenType.KwElse);
		assertKeyword(tokenizer, TokenType.KwIf);
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Percent);
		assertInt(tokenizer, "3", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Equals);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "console");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "println");
		assertToken(tokenizer, TokenType.OpenParen);
		assertString(tokenizer, "Fizz");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertKeyword(tokenizer, TokenType.KwElse);
		assertKeyword(tokenizer, TokenType.KwIf);
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Percent);
		assertInt(tokenizer, "5", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Equals);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "console");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "println");
		assertToken(tokenizer, TokenType.OpenParen);
		assertString(tokenizer, "Buzz");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertKeyword(tokenizer, TokenType.KwElse);
		assertIdentifier(tokenizer, "console");
		assertToken(tokenizer, TokenType.Dot);
		assertIdentifier(tokenizer, "println");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertIdentifier(tokenizer, "main");
		assertToken(tokenizer, TokenType.OpenParen);
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.OpenCurly);
		assertKeyword(tokenizer, TokenType.KwFor);
		assertToken(tokenizer, TokenType.OpenParen);
		assertKeyword(tokenizer, TokenType.KwVar);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Assign);
		assertInt(tokenizer, "0", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Less);
		assertInt(tokenizer, "100", TokenType.DecimalLiteral);
		assertToken(tokenizer, TokenType.Semicolon);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.Increment);
		assertToken(tokenizer, TokenType.CloseParen);
		assertIdentifier(tokenizer, "fizzbuzz");
		assertToken(tokenizer, TokenType.OpenParen);
		assertIdentifier(tokenizer, "i");
		assertToken(tokenizer, TokenType.CloseParen);
		assertToken(tokenizer, TokenType.Semicolon);
		assertToken(tokenizer, TokenType.CloseCurly);
		assertEof(tokenizer);
	}
}
