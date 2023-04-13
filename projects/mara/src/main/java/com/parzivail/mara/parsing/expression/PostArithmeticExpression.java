package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.Token;

public class PostArithmeticExpression extends Expression
{
	public final Expression root;
	public final Token operator;

	public PostArithmeticExpression(Expression root, Token operator)
	{
		super(root.firstToken);
		this.root = root;
		this.operator = operator;
	}
}
