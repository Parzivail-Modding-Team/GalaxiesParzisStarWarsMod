package com.parzivail.mara.lexing;

public enum TokenizeState
{
	Begin,
	NumericLiteral,
	IntegerLiteral,
	BinaryLiteral,
	OctalLiteral,
	HexLiteral,
	PercentOrRightRot,
	GreaterOrRightShiftOrGreaterEquals,
	LessOrLeftShiftOrLeftRotOrLessEquals,
	BangOrNotEquals,
	AssignOrEquals,
	AmpOrBooleanAnd,
	PipeOrBooleanOr,
	Identifier,
	End,
	EndIfTerminated,
	EmitEof,
	Eof
}
