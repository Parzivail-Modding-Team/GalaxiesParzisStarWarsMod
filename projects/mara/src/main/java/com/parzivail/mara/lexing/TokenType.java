package com.parzivail.mara.lexing;

public enum TokenType
{
	Assign, // =
	Equals, // ==
	NotEquals, // !=
	Plus, // +
	Increment, // ++
	Minus, // -
	Decrement, // --
	Asterisk, // *
	Slash, // /
	Backslash, // \
	Tilde, // ~
	Grave, // `
	Bang, // !
	Dot, // .
	Comma, // ,
	Question, // ?
	Coalesce, // ??
	OpenParen, // (
	CloseParen, // )
	OpenSquare, // [
	CloseSquare, // ]
	OpenCurly, // {
	CloseCurly, // }
	Pipe, // |
	Or, // ||
	Amp, // &
	And, // &&
	Percent, // %
	At, // @
	Pound, // #
	Semicolon, // ;
	Colon, // :
	Dollar, // $
	Caret, // ^
	Greater, // >
	GreaterEquals, // >=
	Less, // <
	LessEquals, // <=
	RightArrow, // =>
	RightShift, // >>
	RightRotate, // %>
	LeftShift, // <<
	LeftRotate, // <%
	DecimalLiteral, // 12345
	BinaryLiteral, // 0b10101
	OctalLiteral, // 0c77665
	HexLiteral, // 0xBEEF2
	FloatingPointLiteral, // 12.3456789
	Identifier, // var1
	StringLiteral,
	CharacterLiteral,
	Eof, // <End of file>
	// Keywords
	KwTrue, // true
	KwFalse, // false
	KwVar, // var
	KwIf, // if
	KwElse, // else
	KwFor, // for
	KwUsing, // using
	KwReturn // return
}
