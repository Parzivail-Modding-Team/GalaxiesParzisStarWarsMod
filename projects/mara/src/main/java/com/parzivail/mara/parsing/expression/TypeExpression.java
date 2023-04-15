package com.parzivail.mara.parsing.expression;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TypeExpression extends Expression
{
	public final IdentifierExpression typeName;
	public final ArrayList<TypeExpression> typeArgs;

	public TypeExpression(IdentifierExpression typeName, ArrayList<TypeExpression> typeArgs)
	{
		super(typeName.firstToken);
		this.typeName = typeName;
		this.typeArgs = typeArgs;
	}

	@Override
	public String toString()
	{
		if (typeArgs.isEmpty())
			return String.format("(%s)", typeName.value);

		return String.format("(%s<%s>)", typeName.value, typeArgs.stream().map(TypeExpression::toString).collect(Collectors.joining(", ")));
	}
}
