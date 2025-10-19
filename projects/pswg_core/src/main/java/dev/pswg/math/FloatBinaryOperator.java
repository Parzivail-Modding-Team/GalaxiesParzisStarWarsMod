package dev.pswg.math;

/**
 * Represents a binary operation between two floats
 */
@FunctionalInterface
public interface FloatBinaryOperator
{
	float apply(float a, float b);
}
