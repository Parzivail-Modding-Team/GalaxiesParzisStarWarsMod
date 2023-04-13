package com.parzivail.mara.parsing;

import com.parzivail.mara.lexing.IdentifierToken;

import java.util.ArrayList;

public class MethodCallExpression extends Expression
{
	public final IdentifierToken identifier;
	public final ArrayList<Expression> parameters;

	public MethodCallExpression(IdentifierToken identifier, ArrayList<Expression> parameters)
	{
		super(identifier);
		this.identifier = identifier;
		this.parameters = parameters;
	}
}
