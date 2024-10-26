package dev.pswg.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * A collection of utilities to register game elements like
 * items and blocks
 */
public final class Registrar
{
	/**
	 * Constructs and registers an item with the provided registry key
	 *
	 * @param registryKey The registry key to assign to the item
	 * @param constructor The constructor that will instantiate the item
	 * @param settings    The settings that will be passed to the item
	 * @param <TItem>     The type of item to construct
	 * @param <TSettings> The type of settings to construct the item with
	 *
	 * @return A constructed item with the provided settings, given the corresponding registry key
	 */
	public static <TItem extends Item, TSettings extends Item.Settings> TItem item(Identifier registryKey, Function<TSettings, TItem> constructor, TSettings settings)
	{
		// this cast to TSettings is legal since `registryKey` returns `this`
		//noinspection unchecked
		var item = constructor.apply((TSettings)settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, registryKey)));
		return Registry.register(Registries.ITEM, registryKey, item);
	}

	/**
	 * Constructs and registers an item with the provided registry key
	 *
	 * @param registryKey The registry key to assign to the item
	 * @param constructor The constructor that will instantiate the item
	 * @param <TItem>     The type of item to construct
	 *
	 * @return A constructed item with the provided settings, given the corresponding registry key
	 */
	public static <TItem extends Item> TItem item(Identifier registryKey, Function<Item.Settings, TItem> constructor)
	{
		return item(registryKey, constructor, new Item.Settings());
	}

	/**
	 * Builds and registers an entity type with the provided registry key
	 *
	 * @param registryKey The registry key to assign to the entity type
	 * @param builder     The builder that represents the entity type
	 * @param <T>         The type of entity to build
	 *
	 * @return A built entity type, given the corresponding registry key
	 */
	public static <T extends Entity> EntityType<T> entityType(Identifier registryKey, EntityType.Builder<T> builder)
	{
		return Registry.register(
				Registries.ENTITY_TYPE,
				registryKey,
				builder.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, registryKey))
		);
	}
}
