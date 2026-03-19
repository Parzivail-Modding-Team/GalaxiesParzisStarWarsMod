package dev.pswg.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
	public static <TItem extends Item, TSettings extends Item.Properties> TItem item(ResourceLocation registryKey, Function<TSettings, TItem> constructor, TSettings settings)
	{
		// this cast to TSettings is legal since `registryKey` returns `this`
		//noinspection unchecked
		var item = constructor.apply((TSettings)settings.setId(ResourceKey.create(Registries.ITEM, registryKey)));
		return Registry.register(BuiltInRegistries.ITEM, registryKey, item);
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
	public static <TBlock extends Block, TSettings extends BlockBehaviour.Properties> TBlock blockWithoutItem(ResourceLocation registryKey, Function<TSettings, TBlock> constructor, TSettings settings)
	{
		// this cast to TSettings is legal since `registryKey` returns `this`
		//noinspection unchecked
		var block = constructor.apply((TSettings)settings.setId(ResourceKey.create(Registries.BLOCK, registryKey)));
		return Registry.register(BuiltInRegistries.BLOCK, registryKey, block);
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
	public static <TBlock extends Block, TSettings extends BlockBehaviour.Properties> TBlock block(ResourceLocation registryKey, Function<TSettings, TBlock> constructor, TSettings settings)
	{
		// this cast to TSettings is legal since `registryKey` returns `this`
		// noinspection unchecked
		var block = constructor.apply((TSettings)settings.setId(ResourceKey.create(Registries.BLOCK, registryKey)));
		Registrar.item(registryKey, itemSettings -> new BlockItem(block, itemSettings));
		return Registry.register(BuiltInRegistries.BLOCK, registryKey, block);
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
	public static <TItem extends Item> TItem item(ResourceLocation registryKey, Function<Item.Properties, TItem> constructor)
	{
		return item(registryKey, constructor, new Item.Properties());
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
	public static <T extends Entity> EntityType<T> entityType(ResourceLocation registryKey, EntityType.Builder<T> builder)
	{
		return Registry.register(
				BuiltInRegistries.ENTITY_TYPE,
				registryKey,
				builder.build(ResourceKey.create(Registries.ENTITY_TYPE, registryKey))
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

	public static <T extends BlockEntity> BlockEntityType<T> blockEntity(ResourceLocation registryKey, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks)
	{
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, registryKey, FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build());
	}

	/**
	 * Builds and registers a recipe type with the provided registry key
	 *
	 * @param registryKey The registry key to assign to the recipe type
	 * @param <T>         The type of recipe to build
	 *
	 * @return A built recipe type, given the corresponding registry key
	 */
	public static <T extends Recipe<?>> RecipeType<T> recipeType(ResourceLocation registryKey)
	{
		return Registry.register(BuiltInRegistries.RECIPE_TYPE, registryKey, new RecipeType<T>()
		{
			@Override
			public String toString()
			{
				return registryKey.toString();
			}
		});
	}

	public static <T extends AbstractContainerMenu> MenuType<T> screenHandlerType(ResourceLocation registryKey, MenuType.MenuSupplier<T> factory)
	{
		return Registry.register(BuiltInRegistries.MENU, registryKey, new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
	}

	public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S recipeSerializer(ResourceLocation id, S serializer)
	{
		return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer);
	}
}
