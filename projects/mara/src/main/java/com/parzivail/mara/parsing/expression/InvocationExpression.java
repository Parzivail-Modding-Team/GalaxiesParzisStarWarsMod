package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.token.IdentifierToken;

import java.util.ArrayList;

public class InvocationExpression extends Expression
{
	public final IdentifierToken identifier;
	public final ArrayList<Expression> parameters;

	public InvocationExpression(IdentifierToken identifier, ArrayList<Expression> parameters)
	{
		super(identifier);
		this.identifier = identifier;
		this.parameters = parameters;
	}
}