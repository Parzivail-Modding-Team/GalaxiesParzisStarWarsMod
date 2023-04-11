package com.parzivail.mara.lexing;

public enum TokenizeState
{
	Begin,
	NumericLiteral,
	DecimalLiteral,
	DotOrFloatingPointLiteral,
	FloatingPointLiteral,
	BinaryLiteral,
	OctalLiteral,
	HexLiteral,
	PercentOrRightRot,
	GreaterOrRightShiftOrGreaterEquals,
	LessOrLeftShiftOrLeftRotOrLessEquals,
	BangOrNotEquals,
	AssignOrEqualsOrArrow,
	AmpOrBooleanAnd,
	PipeOrBooleanOr,
	StringLiteral,
	StringEscape,
	StringUnicodeEscape,
	Identifier,
	End,
	EndIfTerminated,
	EmitEof,
	Eof
}
