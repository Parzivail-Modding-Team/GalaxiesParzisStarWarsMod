package com.parzivail.mara.parsing;

import com.parzivail.mara.lexing.IdentifierToken;

public class BooleanLiteralExpression extends Expression
{
	public final IdentifierToken value;

	public BooleanLiteralExpression(IdentifierToken firstToken)
	{
		super(firstToken);
		value = firstToken;
	}
}
