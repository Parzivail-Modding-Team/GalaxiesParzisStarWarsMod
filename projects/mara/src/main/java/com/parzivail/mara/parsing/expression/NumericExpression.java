package com.parzivail.mara.parsing.expression;

import com.parzivail.mara.lexing.token.NumericToken;

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
			case BinaryLiteral -> "0b" + value.value;
			case DecimalLiteral -> value.value;
			case OctalLiteral -> "0c" + value.value;
			case HexLiteral -> "0x" + value.value;
			default -> "<unknown " + value.type + ": " + value.value + ">";
		};
	}
}
