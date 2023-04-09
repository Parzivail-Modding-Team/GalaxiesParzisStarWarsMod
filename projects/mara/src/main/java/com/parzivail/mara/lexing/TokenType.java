package com.parzivail.mara.lexing;

public enum TokenType
{
	Assign, // =
	Equals, // ==
	NotEquals, // !=
	Plus, // +
	Minus, // -
	Asterisk, // *
	Slash, // /
	Backslash, // \
	Tilde, // ~
	Grave, // `
	Bang, // !
	Dot, // .
	Comma, // ,
	Question, // ?
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
	SingleQuote, // '
	DoubleQuote, // "
	Dollar, // $
	Caret, // ^
	Greater, // >
	GreaterEquals, // >=
	Less, // <
	LessEquals, // <=
	RightShift, // >>
	RightRotate, // %>
	LeftShift, // <<
	LeftRotate, // <%
	IntegerLiteral, // 12345
	BinaryLiteral, // 0b10101
	OctalLiteral, // 0c77665
	HexLiteral, // 0xBEEF2
	Identifier, // var1
	Eof, // <End of file>
}
