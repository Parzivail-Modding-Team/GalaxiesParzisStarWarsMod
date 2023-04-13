package com.parzivail.mara.parsing;

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

		var s = new StringBuilder();
		printExpression(s, e, 0);

		s.append(".accept(e);");
		System.out.println(s);
	}

	private static void printExpression(StringBuilder s, Expression e, int tabLevel)
	{
		s.append("\t".repeat(tabLevel));

		if (e instanceof NumericExpression ne)
			switch (ne.value.type)
			{
				case DecimalLiteral -> s.append(String.format("decimal(\"%s\")", ne.value.value));
				case HexLiteral -> s.append(String.format("hex(\"%s\")", ne.value.value));
				default -> s.append(String.format("number(TokenType.%s, \"%s\")", ne.value.type, ne.value.value));
			}
		else if (e instanceof BooleanLiteralExpression ble)
			s.append(String.format("bool(TokenType.%s)", ble.value.type));
		else if (e instanceof IdentifierExpression ie)
			s.append(String.format("id(\"%s\")", ie.value.value));
		else if (e instanceof AssignmentExpression ae)
		{
			s.append("assignment(\n");
			printExpression(s, ae.left, tabLevel + 1);
			s.append(",\n");
			printExpression(s, ae.right, tabLevel + 1);
			s.append("\n").append("\t".repeat(tabLevel)).append(")");
		}
		else if (e instanceof ConditionalExpression ce)
		{
			s.append("ternary(\n");
			printExpression(s, ce.conditional, tabLevel + 1);
			s.append(",\n");
			printExpression(s, ce.truthy, tabLevel + 1);
			s.append(",\n");
			printExpression(s, ce.falsy, tabLevel + 1);
			s.append("\n").append("\t".repeat(tabLevel)).append(")");
		}
		else if (e instanceof IndexerExpression ie)
		{
			s.append("indexer(\n");
			printExpression(s, ie.expression, tabLevel + 1);
			s.append(",\n");
			printExpression(s, ie.indexer, tabLevel + 1);
			s.append("\n").append("\t".repeat(tabLevel)).append(")");
		}
		else if (e instanceof UnaryExpression ue)
		{
			s.append("unary(\n");
			s.append("\t".repeat(tabLevel + 1)).append(String.format("TokenType.%s,\n", ue.operator.type));
			printExpression(s, ue.expression, tabLevel + 1);
			s.append("\n").append("\t".repeat(tabLevel)).append(")");
		}
		else if (e instanceof PreArithmeticExpression pae)
		{
			s.append("pre(\n");
			s.append("\t".repeat(tabLevel + 1)).append(String.format("TokenType.%s,\n", pae.operator.type));
			printExpression(s, pae.root, tabLevel + 1);
			s.append("\n").append("\t".repeat(tabLevel)).append(")");
		}
		else if (e instanceof PostArithmeticExpression pae)
		{
			s.append("post(\n");
			s.append("\t".repeat(tabLevel + 1)).append(String.format("TokenType.%s,\n", pae.operator.type));
			printExpression(s, pae.root, tabLevel + 1);
			s.append("\n").append("\t".repeat(tabLevel)).append(")");
		}
		else if (e instanceof BinaryExpression be)
		{
			s.append("binary(\n");
			printExpression(s, be.left, tabLevel + 1);
			s.append(",\n");
			s.append("\t".repeat(tabLevel + 1)).append(String.format("TokenType.%s,\n", be.operator.type));
			printExpression(s, be.right, tabLevel + 1);
			s.append("\n").append("\t".repeat(tabLevel)).append(")");
		}
		else if (e instanceof MemberAccessExpression mae)
		{
			s.append("member(\n");
			printExpression(s, mae.root, tabLevel + 1);
			s.append(",\n");
			printExpression(s, mae.member, tabLevel + 1);
			s.append("\n").append("\t".repeat(tabLevel)).append(")");
		}
		else
			throw new RuntimeException();
	}
}
