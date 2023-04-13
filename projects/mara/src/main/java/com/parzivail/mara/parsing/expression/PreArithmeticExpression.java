package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.Token;

public class PreArithmeticExpression extends Expression
{
	public final Expression root;
	public final Token operator;

	public PreArithmeticExpression(Token operator, Expression root)
	{
		super(operator);
		this.root = root;
		this.operator = operator;
	}
}
