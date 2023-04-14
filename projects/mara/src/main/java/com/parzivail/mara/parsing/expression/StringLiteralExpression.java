package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.token.StringToken;

public class StringLiteralExpression extends Expression
{
	public final StringToken value;

	public StringLiteralExpression(StringToken token)
	{
		super(token);
		this.value = token;
	}
}
