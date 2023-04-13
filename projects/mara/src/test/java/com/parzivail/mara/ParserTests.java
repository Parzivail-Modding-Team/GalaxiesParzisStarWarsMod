package com.parzivail.mara;

import com.parzivail.mara.lexing.TokenType;
import com.parzivail.mara.lexing.Tokenizer;
import com.parzivail.mara.parsing.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.function.Consumer;

public class ParserTests
{
	private static Consumer<Expression> number(TokenType type, String value)
	{
		return e -> {
			Assertions.assertInstanceOf(NumericExpression.class, e);
			var ne = (NumericExpression)e;
			Assertions.assertEquals(type, ne.value.type);
			Assertions.assertEquals(value, ne.value.value);
		};
	}

	private static Consumer<Expression> decimal(String value)
	{
		return number(TokenType.DecimalLiteral, value);
	}

	private static Consumer<Expression> hex(String value)
	{
		return number(TokenType.HexLiteral, value);
	}

	private static Consumer<Expression> indexer(Consumer<Expression> expression, Consumer<Expression> indexer)
	{
		return e -> {
			Assertions.assertInstanceOf(IndexerExpression.class, e);
			var ie = (IndexerExpression)e;
			expression.accept(ie.expression);
			indexer.accept(ie.indexer);
		};
	}

	private static Consumer<Expression> unary(TokenType op, Consumer<Expression> r)
	{
		return e -> {
			Assertions.assertInstanceOf(UnaryExpression.class, e);
			var ue = (UnaryExpression)e;
			Assertions.assertEquals(op, ue.operator.type);
			r.accept(ue.expression);
		};
	}

	private static Consumer<Expression> binary(Consumer<Expression> l, TokenType op, Consumer<Expression> r)
	{
		return e -> {
			Assertions.assertInstanceOf(BinaryExpression.class, e);
			var be = (BinaryExpression)e;
			l.accept(be.left);
			Assertions.assertEquals(op, be.operator.type);
			r.accept(be.right);
		};
	}

	private static Consumer<Expression> id(String v)
	{
		return e -> {
			Assertions.assertInstanceOf(IdentifierExpression.class, e);
			var ie = (IdentifierExpression)e;
			Assertions.assertEquals(v, ie.value.value);
		};
	}

	@Test
	public void precedence0(TestInfo testInfo)
	{
		var t = new Tokenizer("a + b / c - d");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens());
		binary(
				binary(
						id("a"),
						TokenType.Plus,
						binary(id("b"), TokenType.Slash, id("c"))
				),
				TokenType.Minus,
				id("d")
		).accept(e);
	}

	@Test
	public void precedence1(TestInfo testInfo)
	{
		var t = new Tokenizer("(a + b) / c - d");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens());
		binary(
				binary(
						binary(id("a"), TokenType.Plus, id("b")),
						TokenType.Slash,
						id("c")
				),
				TokenType.Minus,
				id("d")
		).accept(e);
	}

	@Test
	public void precedence2(TestInfo testInfo)
	{
		var t = new Tokenizer("!a || b || c");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens());
		binary(
				binary(
						unary(TokenType.Bang, id("a")),
						TokenType.Or,
						id("b")
				),
				TokenType.Or,
				id("c")
		).accept(e);
	}

	@Test
	public void precedence3(TestInfo testInfo)
	{
		var t = new Tokenizer("~a || b | c");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens());
		binary(
				unary(TokenType.Tilde, id("a")),
				TokenType.Or,
				binary(
						id("b"),
						TokenType.Pipe,
						id("c")
				)
		).accept(e);
	}

	@Test
	public void postfix0(TestInfo testInfo)
	{
		var t = new Tokenizer("a[0xF0 + b]");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens());
		indexer(
				id("a"),
				binary(hex("F0"), TokenType.Plus, id("b"))
		).accept(e);
	}
}
