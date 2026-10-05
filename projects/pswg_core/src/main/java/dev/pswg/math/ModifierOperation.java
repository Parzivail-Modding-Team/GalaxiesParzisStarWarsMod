package dev.pswg.math;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * Supported modifier operations in evaluation order.
 */
public enum ModifierOperation implements StringRepresentable
{
	/**
	 * Adds the literal value to the current stat.
	 */
	ADD("add"),

	/**
	 * Adds a fraction of the base stat.
	 */
	ADD_MULTIPLIED_BASE("add_multiplied_base"),

	/**
	 * Multiplies the current stat by the literal factor.
	 */
	MULTIPLY_TOTAL("multiply_total");

	/**
	 * Strict codec for the closed modifier-operation vocabulary.
	 */
	public static final Codec<ModifierOperation> CODEC = StringRepresentable.fromValues(ModifierOperation::values);

	/**
	 * Serialized operation name.
	 */
	private final String _serializedName;

	/**
	 * Creates one serialized modifier operation.
	 */
	ModifierOperation(String serializedName)
	{
		_serializedName = serializedName;
	}

	/**
	 * Returns the serialized modifier-operation name.
	 */
	@Override
	public String getSerializedName()
	{
		return _serializedName;
	}
}
