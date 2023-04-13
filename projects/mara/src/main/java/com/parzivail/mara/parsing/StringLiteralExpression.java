package com.parzivail.mara.parsing;

import com.parzivail.mara.lexing.StringToken;

public class StringLiteralExpression extends Expression
{
	public final StringToken value;

	public StringLiteralExpression(StringToken token)
	{
		super(token);
		this.value = token;
	}
}
