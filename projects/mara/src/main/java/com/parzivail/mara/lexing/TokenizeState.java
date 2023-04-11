package com.parzivail.mara.lexing;

public enum TokenizeState
{
	Begin,
	Restart,
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
	SlashOrComment,
	LineComment,
	CharacterLiteral,
	StringLiteral,
	StringEscape,
	StringUnicodeEscape,
	Identifier,
	End,
	EndIfTerminated,
	EmitEof,
	Eof
}
