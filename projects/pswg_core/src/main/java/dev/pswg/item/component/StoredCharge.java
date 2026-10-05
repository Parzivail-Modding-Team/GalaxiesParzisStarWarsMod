package dev.pswg.item.component;

import com.google.common.base.Preconditions;
import dev.pswg.codecgenerator.CodecRange;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.generated.codecs.IStoredChargeCodec;

/**
 * Bounded, registry-independent charge value suitable for persistent, synchronized item components.
 *
 * @param current Current stored charge units.
 * @param capacity Maximum stored charge units.
 */
@GenerateCodec(strict = true)
public record StoredCharge(
		@CodecRange(min = 0, max = MAX_UNITS) int current,
		@CodecRange(min = 1, max = MAX_UNITS) int capacity
) implements IStoredChargeCodec
{
	/** Maximum number of units representable by a generic stored-charge value. */
	public static final int MAX_UNITS = 1_000_000;

	/** Validates the unit ranges and prevents the current charge from exceeding capacity. */
	public StoredCharge
	{
		Preconditions.checkArgument(current >= 0 && current <= MAX_UNITS,
				"current must be in 0..%s", MAX_UNITS);
		Preconditions.checkArgument(capacity >= 1 && capacity <= MAX_UNITS,
				"capacity must be in 1..%s", MAX_UNITS);
		Preconditions.checkArgument(current <= capacity,
				"current charge %s cannot exceed capacity %s", current, capacity);
	}
}
