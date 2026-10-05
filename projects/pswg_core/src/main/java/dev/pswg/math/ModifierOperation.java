package dev.pswg.math;

import dev.pswg.codecgenerator.GenerateEnumCodec;
import dev.pswg.generated.codecs.IModifierOperationCodec;

/**
 * Supported modifier operations in evaluation order.
 */
@GenerateEnumCodec
public enum ModifierOperation implements IModifierOperationCodec
{
	/**
	 * Adds the literal value to the current stat.
	 */
	ADD,

	/**
	 * Adds a fraction of the base stat.
	 */
	ADD_MULTIPLIED_BASE,

	/**
	 * Multiplies the current stat by the literal factor.
	 */
	MULTIPLY_TOTAL
}
