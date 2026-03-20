package dev.pswg.toolchain.util.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal JSON parser used to keep the early toolchain bootstrap self-contained.
 */
public final class JsonParser
{
	/**
	 * Prevents construction.
	 */
	private JsonParser()
	{
	}

	/**
	 * Parses a JSON document into Java values.
	 *
	 * @param input the JSON input
	 * @return the parsed value tree
	 */
	public static Object parse(String input)
	{
		return new Parser(input).parseDocument();
	}

	/**
	 * Internal recursive-descent parser.
	 */
	private static final class Parser
	{
		/**
		 * The JSON input.
		 */
		private final String _input;

		/**
		 * The current parser position.
		 */
		private int _index;

		/**
		 * Creates a parser for the given input.
		 *
		 * @param input the JSON input
		 */
		private Parser(String input)
		{
			_input = input;
		}

		/**
		 * Parses the full input as a single JSON document.
		 *
		 * @return the parsed root value
		 */
		private Object parseDocument()
		{
			skipWhitespace();
			Object value = parseValue();
			skipWhitespace();

			if (_index != _input.length())
			{
				throw error("Unexpected trailing content");
			}

			return value;
		}

		/**
		 * Parses the next JSON value.
		 *
		 * @return the parsed value
		 */
		private Object parseValue()
		{
			skipWhitespace();

			if (_index >= _input.length())
			{
				throw error("Unexpected end of input");
			}

			char current = _input.charAt(_index);

			return switch (current)
			{
				case '{' -> parseObject();
				case '[' -> parseArray();
				case '"' -> parseString();
				case 't' -> parseTrue();
				case 'f' -> parseFalse();
				case 'n' -> parseNull();
				default ->
				{
					if (current == '-' || Character.isDigit(current))
					{
						yield parseNumber();
					}

					throw error("Unexpected character: " + current);
				}
			};
		}

		/**
		 * Parses an object value.
		 *
		 * @return the parsed object
		 */
		private Map<String, Object> parseObject()
		{
			expect('{');
			skipWhitespace();

			Map<String, Object> object = new LinkedHashMap<>();

			if (peek('}'))
			{
				expect('}');
				return object;
			}

			while (true)
			{
				skipWhitespace();
				String key = parseString();
				skipWhitespace();
				expect(':');
				Object value = parseValue();
				object.put(key, value);
				skipWhitespace();

				if (peek('}'))
				{
					expect('}');
					return object;
				}

				expect(',');
			}
		}

		/**
		 * Parses an array value.
		 *
		 * @return the parsed array
		 */
		private List<Object> parseArray()
		{
			expect('[');
			skipWhitespace();

			List<Object> array = new ArrayList<>();

			if (peek(']'))
			{
				expect(']');
				return array;
			}

			while (true)
			{
				array.add(parseValue());
				skipWhitespace();

				if (peek(']'))
				{
					expect(']');
					return array;
				}

				expect(',');
			}
		}

		/**
		 * Parses a string value.
		 *
		 * @return the parsed string
		 */
		private String parseString()
		{
			expect('"');
			StringBuilder builder = new StringBuilder();

			while (_index < _input.length())
			{
				char current = _input.charAt(_index++);

				if (current == '"')
				{
					return builder.toString();
				}

				if (current == '\\')
				{
					builder.append(parseEscape());
					continue;
				}

				builder.append(current);
			}

			throw error("Unterminated string");
		}

		/**
		 * Parses an escaped character sequence.
		 *
		 * @return the decoded escaped character
		 */
		private char parseEscape()
		{
			if (_index >= _input.length())
			{
				throw error("Unterminated escape sequence");
			}

			char escape = _input.charAt(_index++);

			return switch (escape)
			{
				case '"', '\\', '/' -> escape;
				case 'b' -> '\b';
				case 'f' -> '\f';
				case 'n' -> '\n';
				case 'r' -> '\r';
				case 't' -> '\t';
				case 'u' -> parseUnicodeEscape();
				default -> throw error("Invalid escape sequence: \\" + escape);
			};
		}

		/**
		 * Parses a unicode escape sequence.
		 *
		 * @return the decoded unicode character
		 */
		private char parseUnicodeEscape()
		{
			if (_index + 4 > _input.length())
			{
				throw error("Incomplete unicode escape");
			}

			String hex = _input.substring(_index, _index + 4);
			_index += 4;
			return (char) Integer.parseInt(hex, 16);
		}

		/**
		 * Parses a numeric value.
		 *
		 * @return the parsed number
		 */
		private Number parseNumber()
		{
			int start = _index;

			if (peek('-'))
			{
				_index++;
			}

			readDigits();

			if (peek('.'))
			{
				_index++;
				readDigits();
			}

			if (peek('e') || peek('E'))
			{
				_index++;

				if (peek('+') || peek('-'))
				{
					_index++;
				}

				readDigits();
			}

			String numberText = _input.substring(start, _index);

			if (numberText.contains(".") || numberText.contains("e") || numberText.contains("E"))
			{
				return Double.parseDouble(numberText);
			}

			return Long.parseLong(numberText);
		}

		/**
		 * Parses the literal {@code true}.
		 *
		 * @return {@code Boolean.TRUE}
		 */
		private Boolean parseTrue()
		{
			expectLiteral("true");
			return Boolean.TRUE;
		}

		/**
		 * Parses the literal {@code false}.
		 *
		 * @return {@code Boolean.FALSE}
		 */
		private Boolean parseFalse()
		{
			expectLiteral("false");
			return Boolean.FALSE;
		}

		/**
		 * Parses the literal {@code null}.
		 *
		 * @return {@code null}
		 */
		private Object parseNull()
		{
			expectLiteral("null");
			return null;
		}

		/**
		 * Reads a contiguous digit sequence.
		 */
		private void readDigits()
		{
			int start = _index;

			while (_index < _input.length() && Character.isDigit(_input.charAt(_index)))
			{
				_index++;
			}

			if (start == _index)
			{
				throw error("Expected digit");
			}
		}

		/**
		 * Skips JSON whitespace characters.
		 */
		private void skipWhitespace()
		{
			while (_index < _input.length() && Character.isWhitespace(_input.charAt(_index)))
			{
				_index++;
			}
		}

		/**
		 * Checks whether the current character matches a target character.
		 *
		 * @param target the target character
		 * @return {@code true} if the current character matches
		 */
		private boolean peek(char target)
		{
			return _index < _input.length() && _input.charAt(_index) == target;
		}

		/**
		 * Consumes an expected character.
		 *
		 * @param expected the expected character
		 */
		private void expect(char expected)
		{
			if (!peek(expected))
			{
				throw error("Expected '" + expected + "'");
			}

			_index++;
		}

		/**
		 * Consumes an expected literal token.
		 *
		 * @param literal the literal token
		 */
		private void expectLiteral(String literal)
		{
			if (!_input.startsWith(literal, _index))
			{
				throw error("Expected \"" + literal + "\"");
			}

			_index += literal.length();
		}

		/**
		 * Creates a parse exception annotated with the current position.
		 *
		 * @param message the error message
		 * @return the parse exception
		 */
		private IllegalArgumentException error(String message)
		{
			return new IllegalArgumentException(message + " at index " + _index);
		}
	}
}
