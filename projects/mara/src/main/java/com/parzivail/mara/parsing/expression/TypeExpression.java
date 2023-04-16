package com.parzivail.mara.parsing.expression;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TypeExpression extends Expression
{
	public final IdentifierExpression typeName;
	public final boolean nullable;
	public final ArrayList<TypeExpression> typeArgs;

	public TypeExpression(IdentifierExpression typeName, boolean nullable, ArrayList<TypeExpression> typeArgs)
	{
		super(typeName.firstToken);
		this.typeName = typeName;
		this.nullable = nullable;
		this.typeArgs = typeArgs;
	}

	@Override
	public String toString()
	{
		if (typeArgs.isEmpty())
			return String.format("(%s%s)", typeName.value, nullable ? "?" : "");

		return String.format("(%s<%s>%s)", typeName.value, typeArgs.stream().map(TypeExpression::toString).collect(Collectors.joining(", ")), nullable ? "?" : "");
	}
}
