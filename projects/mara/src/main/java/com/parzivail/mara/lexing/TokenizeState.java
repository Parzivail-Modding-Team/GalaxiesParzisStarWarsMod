package com.parzivail.mara.lexing;

public enum TokenizeState
{
	Begin,
	NumericLiteral,
	DecimalLiteral,
	DotOrFloatingPointLiteral,
	FloatingPointLiteral,
	FloatingPointExponentStart,
	FloatingPointExponentLiteral,
	BinaryLiteral,
	OctalLiteral,
	HexLiteral,
	Percent,
	Greater,
	Less,
	Bang,
	Assign,
	Amp,
	Pipe,
	Question,
	Slash,
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
