package com.parzivail.mara.parsing;

import com.parzivail.mara.lexing.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Parser
{
	@FunctionalInterface
	private interface ParseFunc
	{
		Expression parse(LinkedList<Token> tokens);
	}

	private static String getMatchingTokens(Predicate<TokenType> kind)
	{
		return Arrays
				.stream(TokenType.values())
				.filter(kind)
				.map(Enum::toString)
				.collect(Collectors.joining(", "));
	}

	private static Predicate<TokenType> requireType(TokenType... tokenTypes)
	{
		return (t) -> Arrays.asList(tokenTypes).contains(t);
	}

	private static Token consumeToken(LinkedList<Token> tokens, Predicate<TokenType> kind)
	{
		var token = tokens.pop();
		if (!kind.test(token.type))
			throw new ParseException(String.format("Unexpected token, expected one of: %s", getMatchingTokens(kind)), token);
		return token;
	}

	private static boolean optionallyConsumeToken(LinkedList<Token> tokens, Predicate<TokenType> kind)
	{
		if (!tokens.isEmpty() && kind.test(tokens.peek().type))
		{
			consumeToken(tokens, kind);
			return true;
		}

		return false;
	}

	private static Expression parseNestedLeftAssoc(LinkedList<Token> tokens, ParseFunc parser, Predicate<TokenType> combiningToken)
	{
		var ex = parser.parse(tokens);
		while (!tokens.isEmpty() && combiningToken.test(tokens.peek().type))
			ex = new BinaryExpression(ex, consumeToken(tokens, combiningToken), parser.parse(tokens));

		return ex;
	}

	private static Expression parseCoalesce(LinkedList<Token> tokens, ParseFunc parser)
	{
		// Right-associative
		var ex = parser.parse(tokens);

		if (optionallyConsumeToken(tokens, requireType(TokenType.Coalesce)))
		{
			var right = parser.parse(tokens);
			return new CoalesceExpression(ex, right);
		}

		return ex;
	}

	private static Expression parseTernary(LinkedList<Token> tokens, ParseFunc parser)
	{
		// Right-associative
		var ex = parser.parse(tokens);

		if (optionallyConsumeToken(tokens, requireType(TokenType.Question)))
		{
			var truthy = parser.parse(tokens);
			consumeToken(tokens, requireType(TokenType.Colon));
			var falsy = parser.parse(tokens);
			return new ConditionalExpression(ex, truthy, falsy);
		}

		return ex;
	}

	private static Expression parseAssignment(LinkedList<Token> tokens, ParseFunc parser)
	{
		// Right-associative
		var ex = parser.parse(tokens);

		var compoundOps = requireType(
				// Arithmetic
				TokenType.Plus,
				TokenType.Minus,
				TokenType.Slash,
				TokenType.Asterisk,
				TokenType.Percent,
				// Boolean logical
				TokenType.And,
				TokenType.Or,
				// Bitwise logical
				TokenType.Amp,
				TokenType.Pipe,
				TokenType.Caret,
				// Bitwise shift
				TokenType.LeftShift,
				TokenType.RightShift,
				TokenType.LeftRotate,
				TokenType.RightRotate,
				// Coalesce
				TokenType.Coalesce
		);

		// TODO: lambda operator

		if (tokens.size() >= 2 && compoundOps.test(tokens.get(0).type) && tokens.get(1).type == TokenType.Assign)
		{
			var op = consumeToken(tokens, compoundOps);
			var value = parser.parse(tokens);
			return new CompoundAssignmentExpression(ex, op, value);
		}

		if (optionallyConsumeToken(tokens, requireType(TokenType.Assign)))
		{
			var value = parser.parse(tokens);
			return new AssignmentExpression(ex, value);
		}

		return ex;
	}

	public static Expression parseExpression(LinkedList<Token> tokens)
	{
		// TODO: ternary, etc
		return parseHierarchicalOperators(tokens);
	}

	public static Expression parseExpression(LinkedList<Token> tokens, TokenType closingToken)
	{
		var expr = parseExpression(tokens);
		consumeToken(tokens, type -> type == closingToken);
		return expr;
	}

	public static Expression parsePrimaryExpression(LinkedList<Token> tokens)
	{
		var token = consumeToken(
				tokens,
				requireType(
						TokenType.DecimalLiteral,
						TokenType.BinaryLiteral,
						TokenType.HexLiteral,
						TokenType.OctalLiteral,
						TokenType.FloatingPointLiteral,
						TokenType.Identifier,
						TokenType.StringLiteral,
						TokenType.CharacterLiteral,
						TokenType.OpenParen,
						TokenType.KwTrue,
						TokenType.KwFalse
				)
		);

		return switch (token.type)
		{
			case KwTrue, KwFalse -> new BooleanLiteralExpression((IdentifierToken)token);
			case StringLiteral -> new StringLiteralExpression((StringToken)token);
			case CharacterLiteral -> new CharacterLiteralExpression((CharacterToken)token);
			case DecimalLiteral, BinaryLiteral, HexLiteral, OctalLiteral, FloatingPointLiteral -> new NumericExpression((NumericToken)token);
			case Identifier -> parseIdentifierOrMethodCall(tokens, (IdentifierToken)token);
			case OpenParen -> parseExpression(tokens, TokenType.CloseParen);
			default -> throw new ParseException("Unexpected token in primary expression", token);
		};
	}

	public static Expression parseIdentifierOrMethodCall(LinkedList<Token> tokens, IdentifierToken identifier)
	{
		if (tokens.getFirst().type != TokenType.OpenParen)
			return new IdentifierExpression(identifier);

		return parseMethodCall(tokens, identifier);
	}

	public static MethodCallExpression parseMethodCall(LinkedList<Token> tokens, IdentifierToken identifier)
	{
		// TODO: allow default parameter identifying (example: convert(a, value2: b))

		var paramList = new ArrayList<Expression>();

		consumeToken(tokens, requireType(TokenType.OpenParen));
		while (true)
		{
			var nextTokenType = tokens.getFirst().type;
			if (nextTokenType == TokenType.CloseParen)
			{
				consumeToken(tokens, requireType(TokenType.CloseParen));
				break;
			}

			if (paramList.size() > 0)
				consumeToken(tokens, requireType(TokenType.Comma));

			var param = parseExpression(tokens);
			paramList.add(param);
		}

		return new MethodCallExpression(identifier, paramList);
	}

	public static Expression parseUnaryExpression(LinkedList<Token> tokens)
	{
		var firstToken = tokens.getFirst();

		var unaryOps = requireType(TokenType.Bang, TokenType.Minus, TokenType.Tilde);
		if (unaryOps.test(firstToken.type))
			return new UnaryExpression(firstToken, consumeToken(tokens, unaryOps), parseUnaryExpression(tokens));

		var pe = parsePrimaryExpression(tokens);
		return tokens.getFirst().type != TokenType.OpenSquare ? pe : parseIndexerExpression(tokens, pe);
	}

	private static IndexerExpression parseIndexerExpression(LinkedList<Token> tokens, Expression primaryExpression)
	{
		consumeToken(tokens, requireType(TokenType.OpenSquare));
		var indexingExpression = parseExpression(tokens, TokenType.CloseSquare);
		return new IndexerExpression(primaryExpression, indexingExpression);
	}

	public static Expression parseHierarchicalOperators(LinkedList<Token> tokens)
	{
		// This group of expressions are listed highest-to-lowest precedence
		// and use lambdas to build a nested parser that respects the order
		// of precedence by encapsulating all higher-precedence operators into
		// the lambda passed to each lower-precedence operator

		// x * y, x / y, x % y
		ParseFunc multiply = t -> parseNestedLeftAssoc(t, Parser::parseUnaryExpression, requireType(TokenType.Asterisk, TokenType.Slash, TokenType.Percent));

		// x + y, x – y
		ParseFunc add = t -> parseNestedLeftAssoc(t, multiply, requireType(TokenType.Plus, TokenType.Minus));

		// x << y, x >> y, x <% y, x %> y
		// TODO? C#: x >>> y
		ParseFunc shift = t -> parseNestedLeftAssoc(t, add, requireType(TokenType.LeftShift, TokenType.RightShift, TokenType.LeftRotate, TokenType.RightRotate));

		// x < y, x > y, x <= y, x >= y
		// TODO? C#: is, as
		ParseFunc compare = t -> parseNestedLeftAssoc(t, shift, requireType(TokenType.Less, TokenType.Greater, TokenType.LessEquals, TokenType.GreaterEquals));

		// x == y, x != y
		ParseFunc equality = t -> parseNestedLeftAssoc(t, compare, requireType(TokenType.Equals, TokenType.NotEquals));

		// x & y
		ParseFunc logicalAnd = t -> parseNestedLeftAssoc(t, equality, requireType(TokenType.Amp));

		// x ^ y
		ParseFunc logicalXor = t -> parseNestedLeftAssoc(t, logicalAnd, requireType(TokenType.Caret));

		// x | y
		ParseFunc logicalOr = t -> parseNestedLeftAssoc(t, logicalXor, requireType(TokenType.Pipe));

		// x && y
		ParseFunc conditionalAnd = t -> parseNestedLeftAssoc(t, logicalOr, requireType(TokenType.And));

		// x || y
		ParseFunc conditionalOr = t -> parseNestedLeftAssoc(t, conditionalAnd, requireType(TokenType.Or));

		// x ?? y
		ParseFunc coalesce = t -> parseCoalesce(tokens, conditionalOr);

		// c ? t : f
		ParseFunc ternary = t -> parseTernary(t, coalesce);

		return parseAssignment(tokens, ternary);
	}
}
