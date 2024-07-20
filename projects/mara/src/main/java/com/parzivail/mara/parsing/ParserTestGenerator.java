package com.parzivail.mara.parsing;

import com.parzivail.mara.lexing.TokenType;
import com.parzivail.mara.lexing.Tokenizer;
import com.parzivail.mara.parsing.expression.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ParserTestGenerator
{
	public static void main(String[] args) throws IOException
	{
		var str = Files.readString(new File(args[0]).toPath());
		var t = new Tokenizer(str);
		t.consumeAll();

		var e = Parser.parseExpression(t.getTokens());
		if (t.getTokens().getFirst().type != TokenType.Eof)
			throw new ParseException("Dangling tokens", t.getTokens().getFirst());

		var s = new StringBuilder();
		printExpression(s, e, 0);

		s.append(".accept(e);");
		System.out.println(s);
	}

	private static void printExpression(StringBuilder s, Expression e, int tabLevel)
	{
		s.repeat('\t', tabLevel);

		switch (e)
		{
			case NumericExpression ne ->
			{
				switch (ne.value.type)
				{
					case DecimalLiteral -> s.append("decimal(\"").append(ne.value.value).append("\")");
					case HexLiteral -> s.append("hex(\"").append(ne.value.value).append("\")");
					default -> s.append("number(TokenType.").append(ne.value.type).append(", \"").append(ne.value.value).append("\")");
				}
			}
			case BooleanLiteralExpression ble -> s.append("bool(TokenType.").append(ble.value.type).append(')');
			case IdentifierExpression ie -> s.append("id(\"").append(ie.value.value).append("\")");
			case AssignmentExpression ae ->
			{
				s.append("assignment(\n");
				printExpression(s, ae.left, tabLevel + 1);
				s.append(",\n");
				printExpression(s, ae.right, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case ConditionalExpression ce ->
			{
				s.append("ternary(\n");
				printExpression(s, ce.conditional, tabLevel + 1);
				s.append(",\n");
				printExpression(s, ce.truthy, tabLevel + 1);
				s.append(",\n");
				printExpression(s, ce.falsy, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case IndexerExpression ie ->
			{
				s.append("indexer(\n");
				printExpression(s, ie.expression, tabLevel + 1);
				s.append(",\n");
				printExpression(s, ie.indexer, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case UnaryExpression ue ->
			{
				s.append("unary(\n");
				s.repeat('\t', tabLevel + 1).append("TokenType.").append(ue.operator.type).append(",\n");
				printExpression(s, ue.expression, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case PreArithmeticExpression pae ->
			{
				s.append("pre(\n");
				s.repeat('\t', tabLevel + 1).append("TokenType.").append(pae.operator.type).append(",\n");
				printExpression(s, pae.root, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case PostArithmeticExpression pae ->
			{
				s.append("post(\n");
				s.repeat('\t', tabLevel + 1).append("TokenType.").append(pae.operator.type).append(",\n");
				printExpression(s, pae.root, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case BinaryExpression be ->
			{
				s.append("binary(\n");
				printExpression(s, be.left, tabLevel + 1);
				s.append(",\n");
				s.repeat('\t', tabLevel + 1).append("TokenType.").append(be.operator.type).append(",\n");
				printExpression(s, be.right, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case MemberAccessExpression mae ->
			{
				s.append("member(\n");
				printExpression(s, mae.root, tabLevel + 1);
				s.append(",\n");
				printExpression(s, mae.member, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case InvocationExpression ie ->
			{
				s.append("invoke(\n");
				printExpression(s, ie.identifier, tabLevel + 1);
				for (var param : ie.parameters)
				{
					s.append(",\n");
					printExpression(s, param, tabLevel + 1);
				}
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case TypeExpression te ->
			{
				s.append("type(\n");
				printExpression(s, te.typeName, tabLevel + 1);
				for (var param : te.typeArgs)
				{
					s.append(",\n");
					printExpression(s, param, tabLevel + 1);
				}
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case CastExpression ce ->
			{
				s.append("cast(\n");
				printExpression(s, ce.type, tabLevel + 1);
				s.append(",\n");
				printExpression(s, ce.value, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			case NullConditionalExpression ce ->
			{
				s.append("nullCond(\n");
				printExpression(s, ce.value, tabLevel + 1);
				s.append('\n').repeat('\t', tabLevel).append(')');
			}
			default -> throw new RuntimeException();
		}
	}
}
