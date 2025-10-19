package dev.pswg.math;

/**
 * Represents a strategy for combining multiple values into a final value
 */
public enum Combinator
{
	/**
	 * The values will be added together, starting at zero
	 */
	ARITHMETIC(0, (a, b) -> a + b),

	/**
	 * The values will be multiplied together, starting at one
	 */
	GEOMETRIC(1, (a, b) -> a * b);

	private final float identity;
	private final FloatBinaryOperator func;

	Combinator(float identity, FloatBinaryOperator func)
	{
		this.identity = identity;
		this.func = func;
	}

	/**
	 * Gets the base value when no other modifier values have been applied
	 *
	 * @return The identity value
	 */
	public float getIdentity()
	{
		return identity;
	}

	/**
	 * Applies the modifier to the value
	 *
	 * @param value    The value to modify
	 * @param modifier The amount by which the value should be modified
	 *
	 * @return The modified value
	 */
	public float combine(float value, float modifier)
	{
		return func.apply(value, modifier);
	}
}
