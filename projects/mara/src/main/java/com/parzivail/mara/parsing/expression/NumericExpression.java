package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.NumericToken;

public class NumericExpression extends Expression
{
	public final NumericToken value;

	public NumericExpression(NumericToken value)
	{
		super(value);
		this.value = value;
	}

	@Override
	public String toString()
	{
		return switch (value.type)
		{
			case BinaryLiteral -> String.format("0b%s", value.value);
			case DecimalLiteral -> value.value;
			case OctalLiteral -> String.format("0c%s", value.value);
			case HexLiteral -> String.format("0x%s", value.value);
			default -> String.format("<unknown %s: %s>", value.type, value.value);
		};
	}
}
