package com.parzivail.mara.parsing.expression;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

	@Override
	public String toString()
	{
		return String.format("(%s(%s))", identifier, parameters.stream().map(Expression::toString).collect(Collectors.joining(", ")));
	}
}
