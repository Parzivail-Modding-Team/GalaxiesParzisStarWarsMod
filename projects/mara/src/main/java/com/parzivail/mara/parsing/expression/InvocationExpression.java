package com.parzivail.mara.parsing.expression;

import java.util.ArrayList;

public class InvocationExpression extends Expression
{
	public final Expression identifier;
	public final ArrayList<Expression> parameters;

	public InvocationExpression(Expression source, ArrayList<Expression> parameters)
	{
		super(source.firstToken);
		this.identifier = source;
		this.parameters = parameters;
	}
}
