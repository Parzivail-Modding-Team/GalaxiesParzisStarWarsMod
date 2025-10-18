package dev.pswg.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
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
	 * Constructs and registers a block with the provided registry key
	 *
	 * @param registryKey The registry key to assign to the block
	 * @param constructor The constructor that will instantiate the block
	 * @param settings    The settings that will be passed to the block
	 * @param <TBlock>    The type of block to construct
	 * @param <TSettings> The type of settings to construct the block with
	 *
	 * @return A constructed block with the provided settings, given the corresponding registry key
	 */
	public static <TBlock extends Block, TSettings extends Block.Settings> TBlock blockWithoutItem(Identifier registryKey, Function<TSettings, TBlock> constructor, TSettings settings)
	{
		// this cast to TSettings is legal since `registryKey` returns `this`
		//noinspection unchecked
		var block = constructor.apply((TSettings)settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, registryKey)));
		return Registry.register(Registries.BLOCK, registryKey, block);
	}

	/**
	 * Constructs and registers a block with an item with the provided registry key
	 *
	 * @param registryKey The registry key to assign to the block
	 * @param constructor The constructor that will instantiate the block
	 * @param settings    The settings that will be passed to the block
	 * @param <TBlock>    The type of block to construct
	 * @param <TSettings> The type of settings to construct the block with
	 *
	 * @return A constructed block with the provided settings, given the corresponding registry key
	 */
	public static <TBlock extends Block, TSettings extends AbstractBlock.Settings> TBlock block(Identifier registryKey, Function<TSettings, TBlock> constructor, TSettings settings)
	{
		// this cast to TSettings is legal since `registryKey` returns `this`
		// noinspection unchecked
		var block = constructor.apply((TSettings)settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, registryKey)));
		Registrar.item(registryKey, itemSettings -> new BlockItem(block, itemSettings));
		return Registry.register(Registries.BLOCK, registryKey, block);
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

	/**
	 * Builds and registers a block entity type with the provided registry key
	 *
	 * @param registryKey The registry key to assign to the block entity type
	 * @param factory     The factory that constructs the block entity
	 * @param <T>         The type of block entity to build
	 *
	 * @return A built block entity type, given the corresponding registry key
	 */

	public static <T extends BlockEntity> BlockEntityType<T> blockEntity(Identifier registryKey, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks)
	{
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, registryKey, FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build());
	}

	/**
	 * Builds and registers a recipe type with the provided registry key
	 *
	 * @param registryKey The registry key to assign to the recipe type
	 * @param <T>         The type of recipe to build
	 *
	 * @return A built recipe type, given the corresponding registry key
	 */
	public static <T extends Recipe<?>> RecipeType<T> recipeType(Identifier registryKey)
	{
		return Registry.register(Registries.RECIPE_TYPE, registryKey, new RecipeType<T>()
		{
			@Override
			public String toString()
			{
				return registryKey.toString();
			}
		});
	}

	public static <T extends ScreenHandler> ScreenHandlerType<T> screenHandlerType(Identifier registryKey, ScreenHandlerType.Factory<T> factory)
	{
		return Registry.register(Registries.SCREEN_HANDLER, registryKey, new ScreenHandlerType<>(factory, FeatureFlags.DEFAULT_ENABLED_FEATURES));
	}

	public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S recipeSerializer(Identifier id, S serializer)
	{
		return Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
	}
}
