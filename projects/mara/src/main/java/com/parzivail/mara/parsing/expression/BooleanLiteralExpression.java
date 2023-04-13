package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.KeywordToken;

public class BooleanLiteralExpression extends Expression
{
	public final KeywordToken value;

	public BooleanLiteralExpression(KeywordToken firstToken)
	{
		super(firstToken);
		value = firstToken;
	}
}
