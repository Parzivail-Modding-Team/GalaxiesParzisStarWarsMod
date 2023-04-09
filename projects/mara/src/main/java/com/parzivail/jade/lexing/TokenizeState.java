package com.parzivail.jade.lexing;

public enum TokenizeState
{
	Begin,
	NumericLiteral,
	IntegerLiteral,
	BinaryLiteral,
	OctalLiteral,
	HexLiteral,
	PercentOrRightRot,
	GreaterOrRightShift,
	LessOrLeftShiftOrLeftRot,
	AssignOrEquals,
	BitwiseOrBooleanAnd,
	BitwiseOrBooleanOr,
	Identifier,
	End,
	EndIfTerminated,
	EmitEof,
	Eof
}
