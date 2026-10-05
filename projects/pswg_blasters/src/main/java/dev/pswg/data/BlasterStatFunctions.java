package dev.pswg.data;

import com.google.common.base.Preconditions;
import dev.pswg.math.ModifierOperation;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * Descriptors for the fixed stat target, unit, allowed operations, and numeric range of attachment functions.
 * Addons register immutable descriptors before the table is frozen during module finalization.
 */
public final class BlasterStatFunctions
{
	/**
	 * Immutable description of one attachment function's stat contract.
	 */
	public record Descriptor(
			Identifier id,
			String target,
			String unit,
			Set<ModifierOperation> allowedOperations,
			float minimumValue,
			float maximumValue
	)
	{
		/**
		 * Returns whether the descriptor permits the specified operation.
		 */
		public boolean allows(ModifierOperation operation)
		{
			return allowedOperations.contains(operation);
		}
	}

	/**
	 * Built-in zoom multiplier function ID.
	 */
	public static final Identifier ZOOM_MULTIPLIER = id("zoom_multiplier");

	/**
	 * Built-in recoil multiplier function ID.
	 */
	public static final Identifier RECOIL_MULTIPLIER = id("recoil_multiplier");

	/**
	 * Built-in spread multiplier function ID.
	 */
	public static final Identifier SPREAD_MULTIPLIER = id("spread_multiplier");

	/**
	 * Built-in cooling multiplier function ID.
	 */
	public static final Identifier COOLING_MULTIPLIER = id("cooling_multiplier");

	/**
	 * Built-in fire-rate multiplier function ID.
	 */
	public static final Identifier FIRE_RATE_MULTIPLIER = id("fire_rate_multiplier");

	/**
	 * Built-in damage-range multiplier function ID.
	 */
	public static final Identifier DAMAGE_RANGE_MULTIPLIER = id("damage_range_multiplier");

	/**
	 * Immutable baked after finalization.
	 */
	private static volatile Map<Identifier, Descriptor> _frozenDescriptors = Map.of();

	/**
	 * Addon and built-in descriptors collected before finalization.
	 */
	private static final Map<Identifier, Descriptor> _registeredDescriptors = new HashMap<>();

	/**
	 * Lock shared by descriptor registration, lookup snapshots, and finalization.
	 */
	private static final Object _typeLock = new Object();

	/**
	 * Whether addon descriptor registration has been frozen.
	 */
	private static volatile boolean _typesFrozen;

	static
	{
		registerBuiltin(new Descriptor(ZOOM_MULTIPLIER, "stats.zoom", "multiplier", Set.of(ModifierOperation.MULTIPLY_TOTAL), Float.MIN_VALUE, Float.MAX_VALUE));
		registerBuiltin(new Descriptor(RECOIL_MULTIPLIER, "stats.recoil", "multiplier", Set.of(ModifierOperation.MULTIPLY_TOTAL), 0.0F, Float.MAX_VALUE));
		registerBuiltin(new Descriptor(SPREAD_MULTIPLIER, "stats.spread", "multiplier", Set.of(ModifierOperation.MULTIPLY_TOTAL), 0.0F, Float.MAX_VALUE));
		registerBuiltin(new Descriptor(COOLING_MULTIPLIER, "stats.cooling", "multiplier", Set.of(ModifierOperation.MULTIPLY_TOTAL), 0.0F, Float.MAX_VALUE));
		registerBuiltin(new Descriptor(FIRE_RATE_MULTIPLIER, "stats.automaticRepeatDelay", "multiplier", Set.of(ModifierOperation.MULTIPLY_TOTAL), Float.MIN_VALUE, Float.MAX_VALUE));
		registerBuiltin(new Descriptor(DAMAGE_RANGE_MULTIPLIER, "stats.damageRange", "multiplier", Set.of(ModifierOperation.MULTIPLY_TOTAL), Float.MIN_VALUE, Float.MAX_VALUE));
	}

	/**
	 * Prevents instances of this descriptor registry.
	 */
	private BlasterStatFunctions()
	{
	}

	/**
	 * Registers an immutable addon descriptor before attachment definitions are decoded and the table is frozen.
	 */
	public static void register(Descriptor descriptor)
	{
		Objects.requireNonNull(descriptor, "descriptor");
		synchronized (_typeLock)
		{
			Preconditions.checkArgument(!_typesFrozen, "Attachment stat-function registration has been frozen");
			Preconditions.checkArgument(!_registeredDescriptors.containsKey(descriptor.id()),
			                            "Attachment stat function is already registered: %s", descriptor.id());
			_registeredDescriptors.put(descriptor.id(), descriptor);
		}
	}

	/**
	 * Freezes the descriptor table after addon registration has completed; repeated calls are harmless.
	 */
	public static void freezeTypes()
	{
		synchronized (_typeLock)
		{
			if (!_typesFrozen)
			{
				_frozenDescriptors = Map.copyOf(_registeredDescriptors);
				_typesFrozen = true;
			}
		}
	}

	/**
	 * Returns an immutable view of all descriptors registered so far.
	 */
	public static Map<Identifier, Descriptor> descriptors()
	{
		if (_typesFrozen)
			return _frozenDescriptors;

		synchronized (_typeLock)
		{
			return _typesFrozen ? _frozenDescriptors : Map.copyOf(_registeredDescriptors);
		}
	}

	/**
	 * Finds a function descriptor by ID.
	 */
	public static Optional<Descriptor> find(Identifier id)
	{
		Objects.requireNonNull(id, "id");
		return Optional.ofNullable(descriptors().get(id));
	}

	/**
	 * Requires a registered function descriptor.
	 */
	public static Descriptor require(Identifier id)
	{
		return find(id).orElseThrow(() -> new IllegalArgumentException("Unknown blaster attachment function: " + id));
	}

	/**
	 * Validates that a modifier uses a registered function's allowed operation and finite numeric range.
	 */
	public static void validate(Identifier function, ModifierOperation operation, float value)
	{
		var descriptor = require(function);
		Objects.requireNonNull(operation, "operation");
		Preconditions.checkArgument(descriptor.allows(operation),
		                            "Attachment function %s does not allow operation %s", function, operation.getSerializedName());
		Preconditions.checkArgument(Float.isFinite(value)
		                            && value >= descriptor.minimumValue()
		                            && value <= descriptor.maximumValue(),
		                            "Attachment function %s value must be finite and in [%s, %s]",
		                            function, descriptor.minimumValue(), descriptor.maximumValue());
	}

	/**
	 * Inserts one built-in descriptor while the class initializes.
	 */
	private static void registerBuiltin(Descriptor descriptor)
	{
		_registeredDescriptors.put(descriptor.id(), descriptor);
	}

	/**
	 * Creates a blaster-module identifier.
	 */
	private static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath("pswg_blasters", path);
	}
}
