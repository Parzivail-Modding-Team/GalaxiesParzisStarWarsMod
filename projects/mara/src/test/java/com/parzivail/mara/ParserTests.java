package com.parzivail.mara;

import com.parzivail.mara.lexing.TokenType;
import com.parzivail.mara.lexing.Tokenizer;
import com.parzivail.mara.parsing.Parser;
import com.parzivail.mara.parsing.expression.*;
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

	private static Consumer<Expression> assignment(Consumer<Expression> left, Consumer<Expression> right)
	{
		return e -> {
			Assertions.assertInstanceOf(AssignmentExpression.class, e);
			var ae = (AssignmentExpression)e;
			left.accept(ae.left);
			right.accept(ae.right);
		};
	}

	private static Consumer<Expression> ternary(Consumer<Expression> condition, Consumer<Expression> truthy, Consumer<Expression> falsy)
	{
		return e -> {
			Assertions.assertInstanceOf(ConditionalExpression.class, e);
			var ce = (ConditionalExpression)e;
			condition.accept(ce.conditional);
			truthy.accept(ce.truthy);
			falsy.accept(ce.falsy);
		};
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

	private static Consumer<Expression> nullCond(Consumer<Expression> value)
	{
		return e -> {
			Assertions.assertInstanceOf(NullConditionalExpression.class, e);
			var nce = (NullConditionalExpression)e;
			value.accept(nce.value);
		};
	}

	@SafeVarargs
	private static Consumer<Expression> invoke(Consumer<Expression> identifier, Consumer<Expression>... params)
	{
		return e -> {
			Assertions.assertInstanceOf(InvocationExpression.class, e);
			var ie = (InvocationExpression)e;
			identifier.accept(ie.identifier);
			Assertions.assertEquals(params.length, ie.parameters.size());

			for (var i = 0; i < params.length; i++)
				params[i].accept(ie.parameters.get(i));
		};
	}

	private static Consumer<Expression> cast(Consumer<Expression> type, Consumer<Expression> value)
	{
		return e -> {
			Assertions.assertInstanceOf(CastExpression.class, e);
			var ce = (CastExpression)e;
			type.accept(ce.type);
			value.accept(ce.value);
		};
	}

	@SafeVarargs
	private static Consumer<Expression> type(Consumer<Expression> name, Consumer<Expression>... args)
	{
		return e -> {
			Assertions.assertInstanceOf(TypeExpression.class, e);
			var te = (TypeExpression)e;
			name.accept(te.typeName);
			Assertions.assertEquals(args.length, te.typeArgs.size());

			for (var i = 0; i < args.length; i++)
			{
				var arg = te.typeArgs.get(i);
				Assertions.assertInstanceOf(TypeExpression.class, arg);
				args[i].accept(arg);
			}
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

	private static Consumer<Expression> pre(TokenType op, Consumer<Expression> r)
	{
		return e -> {
			Assertions.assertInstanceOf(PreArithmeticExpression.class, e);
			var pae = (PreArithmeticExpression)e;
			Assertions.assertEquals(op, pae.operator.type);
			r.accept(pae.root);
		};
	}

	private static Consumer<Expression> post(TokenType op, Consumer<Expression> r)
	{
		return e -> {
			Assertions.assertInstanceOf(PostArithmeticExpression.class, e);
			var pae = (PostArithmeticExpression)e;
			Assertions.assertEquals(op, pae.operator.type);
			r.accept(pae.root);
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

	private static Consumer<Expression> member(Consumer<Expression> root, Consumer<Expression> member)
	{
		return e -> {
			Assertions.assertInstanceOf(MemberAccessExpression.class, e);
			var mae = (MemberAccessExpression)e;
			root.accept(mae.root);
			member.accept(mae.member);
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

	private static Consumer<Expression> bool(TokenType tokenType)
	{
		return e -> {
			Assertions.assertInstanceOf(BooleanLiteralExpression.class, e);
			var ble = (BooleanLiteralExpression)e;
			Assertions.assertEquals(tokenType, ble.value.type);
		};
	}

	@Test
	public void precedence0(TestInfo testInfo)
	{
		var t = new Tokenizer("a + b / c - d");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
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
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
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
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
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
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
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
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		indexer(
				id("a"),
				binary(hex("F0"), TokenType.Plus, id("b"))
		).accept(e);
	}

	@Test
	public void assignment0(TestInfo testInfo)
	{
		var t = new Tokenizer("a = b");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		assignment(
				id("a"),
				id("b")
		).accept(e);
	}

	@Test
	public void assignment1(TestInfo testInfo)
	{
		var t = new Tokenizer("a = b + 1");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		assignment(
				id("a"),
				binary(id("b"), TokenType.Plus, decimal("1"))
		).accept(e);
	}

	@Test
	public void assignment2(TestInfo testInfo)
	{
		var t = new Tokenizer("a = c ? t + 1 : !f");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		assignment(
				id("a"),
				ternary(
						id("c"),
						binary(id("t"), TokenType.Plus, decimal("1")),
						unary(TokenType.Bang, id("f"))
				)
		).accept(e);
	}

	@Test
	public void memberAccess0(TestInfo testInfo)
	{
		var t = new Tokenizer("mara.object.thing[other.index + 1] * (vecA + vecB).x");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		binary(
				indexer(
						member(
								member(
										id("mara"),
										id("object")
								),
								id("thing")
						),
						binary(
								member(
										id("other"),
										id("index")
								),
								TokenType.Plus,
								decimal("1")
						)
				),
				TokenType.Asterisk,
				member(
						binary(
								id("vecA"),
								TokenType.Plus,
								id("vecB")
						),
						id("x")
				)
		).accept(e);
	}

	@Test
	public void memberAccess1(TestInfo testInfo)
	{
		var t = new Tokenizer("(object.array1 + object.array2)[++objects.length % 5]");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		indexer(
				binary(
						member(
								id("object"),
								id("array1")
						),
						TokenType.Plus,
						member(
								id("object"),
								id("array2")
						)
				),
				binary(
						pre(
								TokenType.Increment,
								member(
										id("objects"),
										id("length")
								)
						),
						TokenType.Percent,
						decimal("5")
				)
		).accept(e);
	}

	@Test
	public void precedence4(TestInfo testInfo)
	{
		var t = new Tokenizer("x = (!false || (thing.value | booleanArray[~flipped >> 3])) != (condition ? otherArray[cfg.index %> 2] : (y = z++))");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		assignment(
				id("x"),
				binary(
						binary(
								unary(
										TokenType.Bang,
										bool(TokenType.KwFalse)
								),
								TokenType.Or,
								binary(
										member(
												id("thing"),
												id("value")
										),
										TokenType.Pipe,
										indexer(
												id("booleanArray"),
												binary(
														unary(
																TokenType.Tilde,
																id("flipped")
														),
														TokenType.RightShift,
														decimal("3")
												)
										)
								)
						),
						TokenType.NotEquals,
						ternary(
								id("condition"),
								indexer(
										id("otherArray"),
										binary(
												member(
														id("cfg"),
														id("index")
												),
												TokenType.RightRotate,
												decimal("2")
										)
								),
								assignment(
										id("y"),
										post(
												TokenType.Increment,
												id("z")
										)
								)
						)
				)
		).accept(e);
	}

	@Test
	public void precedence5(TestInfo testInfo)
	{
		var t = new Tokenizer("arr[1..^2]");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		indexer(
				id("arr"),
				binary(
						decimal("1"),
						TokenType.Range,
						unary(
								TokenType.Caret,
								decimal("2")
						)
				)
		).accept(e);
	}

	@Test
	public void precedence6(TestInfo testInfo)
	{
		var t = new Tokenizer("numbers[start..(start + amountToTake)]");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		indexer(
				id("numbers"),
				binary(
						id("start"),
						TokenType.Range,
						binary(
								id("start"),
								TokenType.Plus,
								id("amountToTake")
						)
				)
		).accept(e);
	}

	@Test
	public void precedence7(TestInfo testInfo)
	{
		var t = new Tokenizer("numbers[..^amountToDrop]");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		indexer(
				id("numbers"),
				unary(
						TokenType.Range,
						unary(
								TokenType.Caret,
								id("amountToDrop")
						)
				)
		).accept(e);
	}

	@Test
	public void precedence8(TestInfo testInfo)
	{
		var t = new Tokenizer("++i++");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		pre(
				TokenType.Increment,
				post(
						TokenType.Increment,
						id("i")
				)
		).accept(e);
	}

	@Test
	public void memberMethodCall(TestInfo testInfo)
	{
		var t = new Tokenizer("someClass.aMethod(1+1, other.thing[1..^(a/b+c)], !true ^ !!false)");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		invoke(
				member(
						id("someClass"),
						id("aMethod")
				),
				binary(
						decimal("1"),
						TokenType.Plus,
						decimal("1")
				),
				indexer(
						member(
								id("other"),
								id("thing")
						),
						binary(
								decimal("1"),
								TokenType.Range,
								unary(
										TokenType.Caret,
										binary(
												binary(
														id("a"),
														TokenType.Slash,
														id("b")
												),
												TokenType.Plus,
												id("c")
										)
								)
						)
				),
				binary(
						unary(
								TokenType.Bang,
								bool(TokenType.KwTrue)
						),
						TokenType.Caret,
						unary(
								TokenType.Bang,
								unary(
										TokenType.Bang,
										bool(TokenType.KwFalse)
								)
						)
				)
		).accept(e);
	}

	@Test
	public void localMethodCall(TestInfo testInfo)
	{
		var t = new Tokenizer("aMethod(1+1, other.thing[1..^(a/b+c)], !true ^ !!false)");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		invoke(
				id("aMethod"),
				binary(
						decimal("1"),
						TokenType.Plus,
						decimal("1")
				),
				indexer(
						member(
								id("other"),
								id("thing")
						),
						binary(
								decimal("1"),
								TokenType.Range,
								unary(
										TokenType.Caret,
										binary(
												binary(
														id("a"),
														TokenType.Slash,
														id("b")
												),
												TokenType.Plus,
												id("c")
										)
								)
						)
				),
				binary(
						unary(
								TokenType.Bang,
								bool(TokenType.KwTrue)
						),
						TokenType.Caret,
						unary(
								TokenType.Bang,
								unary(
										TokenType.Bang,
										bool(TokenType.KwFalse)
								)
						)
				)
		).accept(e);
	}

	@Test
	public void memberMethodCallWithoutParams(TestInfo testInfo)
	{
		var t = new Tokenizer("someClass.someMethod()");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		invoke(
				member(
						id("someClass"),
						id("someMethod")
				)
		).accept(e);
	}

	@Test
	public void localMethodCallWithoutParams(TestInfo testInfo)
	{
		var t = new Tokenizer("someMethod()");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		invoke(
				id("someMethod")
		).accept(e);
	}

	@Test
	public void cast0(TestInfo testInfo)
	{
		var t = new Tokenizer("(otherClass)thing");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		cast(
				type(
						id("otherClass")
				),
				id("thing")
		).accept(e);
	}

	@Test
	public void cast1(TestInfo testInfo)
	{
		var t = new Tokenizer("(otherClass)someMethod((str)obj, (double)1 + 2)");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		cast(
				type(
						id("otherClass")
				),
				invoke(
						id("someMethod"),
						cast(
								type(
										id("str")
								),
								id("obj")
						),
						binary(
								cast(
										type(
												id("double")
										),
										decimal("1")
								),
								TokenType.Plus,
								decimal("2")
						)
				)
		).accept(e);
	}

	@Test
	public void cast2(TestInfo testInfo)
	{
		var t = new Tokenizer("(otherClass<arg1, arg2>)place[0x100 + 1]");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		cast(
				type(
						id("otherClass"),
						type(
								id("arg1")
						),
						type(
								id("arg2")
						)
				),
				indexer(
						id("place"),
						binary(
								hex("100"),
								TokenType.Plus,
								decimal("1")
						)
				)
		).accept(e);
	}

	@Test
	public void nullConditionals(TestInfo testInfo)
	{
		var t = new Tokenizer("thing1?.thing2?[3 + 2]?.callMethod(a, b, c)");
		t.consumeAll();
		var e = Parser.parseExpression(t.getTokens(), TokenType.Eof);
		invoke(
				member(
						nullCond(
								indexer(
										nullCond(
												member(
														nullCond(
																id("thing1")
														),
														id("thing2")
												)
										),
										binary(
												decimal("3"),
												TokenType.Plus,
												decimal("2")
										)
								)
						),
						id("callMethod")
				),
				id("a"),
				id("b"),
				id("c")
		).accept(e);
	}
}
