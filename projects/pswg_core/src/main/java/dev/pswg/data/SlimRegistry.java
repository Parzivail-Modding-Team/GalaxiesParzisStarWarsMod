package dev.pswg.data;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.*;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

/**
 * A registry to manage items keyed by unique identifiers with no
 * support for serializing or syncing the values.
 *
 * @param <TItem> The type of items to be registered.
 */
public class SlimRegistry<TItem> implements Keyable
{
	/**
	 * The backing identifier-to-item map
	 */
	private final Map<ResourceLocation, TItem> registry = new HashMap<>();

	/**
	 * Indicates whether the registry is frozen. When the registry is frozen,
	 * no modifications are allowed.
	 */
	private boolean frozen;

	/**
	 * Registers an item with the given key in the registry.
	 *
	 * @param key   The key for the item. Must not be null.
	 * @param value The item to register. Must not be null.
	 *
	 * @throws IllegalStateException if the registry contains a definition for the given key
	 *                               or if the registry is frozen.
	 * @throws NullPointerException  if the key or value is null.
	 */
	public void register(ResourceLocation key, TItem value)
	{
		throwIfFrozen();
		Objects.requireNonNull(key, "Key cannot be null");
		Objects.requireNonNull(value, "Value cannot be null");

		if (registry.containsKey(key))
			throw new IllegalStateException("Registry already contains a definition for key: " + key);

		registry.put(key, value);
	}

	/**
	 * Unregisters an item from the registry associated with the specified key.
	 *
	 * @param key The key for the item to be unregistered. Must not be null.
	 *
	 * @return true if the item was successfully unregistered, false if no such item existed.
	 *
	 * @throws NullPointerException  if the key is null.
	 * @throws IllegalStateException if the registry is frozen and modifications are not allowed.
	 */
	public boolean unregister(ResourceLocation key)
	{
		throwIfFrozen();
		Objects.requireNonNull(key, "Key cannot be null");
		return registry.remove(key) != null;
	}

	/**
	 * Retrieves an item from the registry based on the provided key.
	 *
	 * @param key The key for the item to retrieve. Must not be null.
	 *
	 * @return The item associated with the specified key.
	 *
	 * @throws NullPointerException  if the key is null.
	 * @throws IllegalStateException if no item is found for the given key.
	 */
	public TItem getValueOrThrow(ResourceLocation key)
	{
		Objects.requireNonNull(key, "Key cannot be null");

		TItem value = registry.get(key);

		if (value == null)
			throw new IllegalStateException("No value found for key: " + key);

		return value;
	}

	/**
	 * Attempts to retrieve an item from the registry based on the provided key.
	 *
	 * @param key The key for the item to retrieve. Must not be null.
	 *
	 * @return An Optional containing the item associated with the specified key, or an empty Optional if no such item exists.
	 *
	 * @throws NullPointerException if the key is null.
	 */
	public Optional<TItem> tryGetValue(ResourceLocation key)
	{
		Objects.requireNonNull(key, "Key cannot be null");
		return Optional.ofNullable(registry.get(key));
	}

	/**
	 * Checks if the registry contains an entry with the specified key.
	 *
	 * @param key the key to be checked for its presence in the registry. Must not be null.
	 *
	 * @return true if the registry contains an entry with the specified key, false otherwise.
	 */
	public boolean containsKey(ResourceLocation key)
	{
		Objects.requireNonNull(key, "Key cannot be null");
		return registry.containsKey(key);
	}

	/**
	 * @return The number of items in the registry.
	 */
	public int size()
	{
		return registry.size();
	}

	/**
	 * Freezes the registry, preventing any further modifications.
	 */
	public void freeze()
	{
		this.frozen = true;
	}

	/**
	 * Checks if the registry is frozen and throws an IllegalStateException if it is.
	 *
	 * @throws IllegalStateException if the registry is frozen and cannot be modified.
	 */
	private void throwIfFrozen()
	{
		if (frozen)
		{
			throw new IllegalStateException("Registry is frozen and cannot be modified");
		}
	}

	/**
	 * Returns a set of all identifiers in the registry.
	 *
	 * @return The set of Identifiers in this registry
	 */
	public Set<ResourceLocation> getIds()
	{
		return registry.keySet();
	}

	@Override
	public <TOps> Stream<TOps> keys(DynamicOps<TOps> ops)
	{
		return getIds().stream().map((id) -> ops.createString(id.toString()));
	}
}
