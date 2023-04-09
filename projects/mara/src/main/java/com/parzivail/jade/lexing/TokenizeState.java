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
	LessOrLeftShiftRot,
	AssignOrEquals,
	Identifier,
	End,
	EndIfTerminated,
	EmitEof,
	Eof
}
