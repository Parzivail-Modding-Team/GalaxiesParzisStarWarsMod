package com.parzivail.datagen.tarkin;

import com.parzivail.datagen.AssetUtils;
import com.parzivail.datagen.FilesystemUtils;
import com.parzivail.datagen.tarkin.config.PswgTarkin;
import com.parzivail.datagen.tarkin.config.TcwTarkin;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgTags;
import com.parzivail.tarkin.api.*;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.block.IPicklingBlock;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.block.Block;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * T.A.R.K.I.N. - Text Asset Record-Keeping, Integration, and Normalization
 */
public class Tarkin
{
	public static final Lumberjack LOG = new Lumberjack("TARKIN");

	public static void main() throws Exception
	{
		FilesystemUtils.setOutputRoot(Path.of(System.getenv("TARKIN_OUT_DIR")));

		List<BuiltAsset> assets = new ArrayList<>();

		var tarkinModid = System.getProperty("tarkin", "");

		switch (tarkinModid)
		{
			case "pswg" -> PswgTarkin.build(assets);
			case "pswg_addon_clonewars" -> TcwTarkin.build(assets);
		}

		FilesystemUtils.nukeRecipeDir();
		FilesystemUtils.nukeBlockstateDir();
		FilesystemUtils.nukeBlockModelJsons();
		FilesystemUtils.nukeItemModelJsons();
		FilesystemUtils.nukeBlockLootTables();
		FilesystemUtils.nukeTags();

		for (var asset : assets)
		{
			LOG.log("Wrote %s", asset.getFilename());
			asset.write();
		}

		LOG.log("Wrote %s assets", assets.size());

		// Synchronize the keys of the en_us locale
		BuiltAsset.mergeLanguageKeys(Identifier.of(tarkinModid, LanguageProvider.OUTPUT_LOCALE), Identifier.of(tarkinModid, LanguageProvider.TARGET_LOCALE));
		LOG.log("Merged language keys");

		LOG.log("Done");
	}

	public static void generateLangFromConfigAnnotations(LanguageBuilder autoconfigOption, List<BuiltAsset> assets, Class<?> config)
	{
		var subclasses = Arrays.asList(config.getDeclaredClasses());

		for (var field : config.getDeclaredFields())
		{
			var fieldLang = autoconfigOption.dot(field.getName());
			fieldLang.build(assets);

			if (field.isAnnotationPresent(ConfigEntry.Gui.Tooltip.class))
			{
				String defaultValue = null;

				var commentAnnotation = field.getAnnotation(Comment.class);
				if (commentAnnotation != null)
					defaultValue = commentAnnotation.value();

				fieldLang.dot("@Tooltip").build(assets, defaultValue);
			}

			if (subclasses.contains(field.getType()))
			{
				var subclassLang = autoconfigOption.dot(field.getName());
				generateLangFromConfigAnnotations(subclassLang, assets, field.getType());
			}
		}
	}

	public static <T, TA extends Annotation> void consumeFields(Class<?> rootClazz, Class<T> registryType, Consumer<T> consumer)
	{
		for (var field : rootClazz.getFields())
		{
			if (!Modifier.isStatic(field.getModifiers()) || !registryType.isAssignableFrom(field.getType()))
				continue;

			try
			{
				consumer.accept((T)field.get(null));
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		for (var clazz : rootClazz.getClasses())
			consumeFields(clazz, registryType, consumer);
	}

	public static void registerEntitiesLang(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeFields(rootClazz, EntityType.class, entry -> {
			var id = Registries.ENTITY_TYPE.getId(entry);
			new LanguageBuilder(Resources.id(LanguageProvider.OUTPUT_LOCALE), "entity").dot(id.getNamespace()).dot(id.getPath()).build(assets);
		});
	}

	public static void registerItemGroupsLang(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeFields(rootClazz, ItemGroup.class, entry -> {
			new LanguageBuilder(Resources.id(LanguageProvider.OUTPUT_LOCALE), ((TranslatableTextContent)entry.getDisplayName().getContent()).getKey()).build(assets);
		});
	}

	public static void registerKeyBindingsLang(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeFields(rootClazz, KeyBinding.class, entry -> {
			new LanguageBuilder(Resources.id(LanguageProvider.OUTPUT_LOCALE), entry.getTranslationKey()).build(assets);
		});
	}

	public static void registerDeathMessageLang(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeFields(rootClazz, RegistryKey.class, entry -> {
			new LanguageBuilder(Resources.id(LanguageProvider.OUTPUT_LOCALE), "death").dot("attack").dot(entry.getValue().getNamespace()).dot(entry.getValue().getPath()).build(assets);
		});
	}

	public static void registerContainersLang(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeFields(rootClazz, ScreenHandlerType.class, entry -> {
			var id = Registries.SCREEN_HANDLER.getId(entry);
			new LanguageBuilder(Resources.id(LanguageProvider.OUTPUT_LOCALE), "container").dot(id.getNamespace()).dot(id.getPath()).build(assets);
		});
	}

	public static <T, TA extends Annotation> void consumeAnnotatedFields(Class<TA> annotationClazz, Class<?> rootClazz, Class<T> registryType, BiConsumer<T, TA> consumer)
	{
		for (var field : rootClazz.getFields())
		{
			var annotation = field.getAnnotation(annotationClazz);
			if (!Modifier.isStatic(field.getModifiers()) || annotation == null || !registryType.isAssignableFrom(field.getType()))
				continue;

			try
			{
				consumer.accept((T)field.get(null), annotation);
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		for (var clazz : rootClazz.getClasses())
			consumeAnnotatedFields(annotationClazz, clazz, registryType, consumer);
	}

	public static void registerLangFields(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeAnnotatedFields(TarkinLang.class, rootClazz, String.class, (s, a) -> {
			new LanguageBuilder(Resources.id(LanguageProvider.OUTPUT_LOCALE), s).build(assets);
		});
	}

	private static TagKey<Item> getTagKey(TrItemTag preset)
	{
		return switch (preset)
		{
			case TrinketsChestBack -> TagKey.of(RegistryKeys.ITEM, Identifier.of("trinkets", "chest/back"));
			case Leaves -> ItemTags.LEAVES;
			case Logs -> ItemTags.LOGS;
			case LogsThatBurn -> ItemTags.LOGS_THAT_BURN;
			case Sand -> ItemTags.SAND;
			default -> throw new RuntimeException("Unsupported value " + preset);
		};
	}

	private static TagKey<Block> getTagKey(TrBlockTag preset)
	{
		return switch (preset)
		{
			case PickaxeMineable -> BlockTags.PICKAXE_MINEABLE;
			case ShearsMineable -> FabricMineableTags.SHEARS_MINEABLE;
			case ShovelMineable -> BlockTags.SHOVEL_MINEABLE;
			case AxeMineable -> BlockTags.AXE_MINEABLE;
			case SlidingDoor -> SwgTags.Blocks.SLIDING_DOORS;
			case Leaves -> BlockTags.LEAVES;
			case DeadBushSubstrate -> BlockTags.DEAD_BUSH_MAY_PLACE_ON;
			case BlasterDestroy -> SwgTags.Blocks.BLASTER_DESTROY;
			case BlasterExplode -> SwgTags.Blocks.BLASTER_EXPLODE;
			case BlasterReflect -> SwgTags.Blocks.BLASTER_REFLECT;
			case Logs -> BlockTags.LOGS;
			case LogsThatBurn -> BlockTags.LOGS_THAT_BURN;
			case Sand -> BlockTags.SAND;
			case Stairs -> BlockTags.STAIRS;
			default -> throw new RuntimeException("Unsupported value " + preset);
		};
	}

	public static void registerItemFields(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeAnnotatedFields(TarkinItem.class, rootClazz, Item.class, (item, a) -> {
			var gen = new ItemGenerator(item);

			switch (a.lang())
			{
				case Item -> gen.lang(LanguageProvider::item);
				default -> throw new RuntimeException("Unsupported value " + a.lang());
			}

			for (var tag : a.tags())
				gen.tag(getTagKey(tag));

			switch (a.model())
			{
				case Empty -> gen.model(ModelFile::empty);
				case Sprite -> gen.model(ModelFile::item);
				case SpawnEgg -> gen.model(ModelFile::spawn_egg);
				case HandheldItem -> gen.model(ModelFile::handheld_item);
				default -> throw new RuntimeException("Unsupported value " + a.model());
			}

			gen.build(assets);
		});
	}

	public static void registerBlockFields(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeAnnotatedFields(TarkinBlock.class, rootClazz, Block.class, (block, a) -> {
			var gen = new BlockGenerator(block);

			switch (a.lang())
			{
				case Block -> gen.lang(LanguageProvider::block);
				default -> throw new RuntimeException("Unsupported value " + a.lang());
			}

			switch (a.state())
			{
				case None ->
				{
				}
				case AxisRotated -> gen.state(BlockStateModelGenerator::createAxisRotatedBlockState);
				case Singleton -> gen.state(BlockStateModelGenerator::createSingletonBlockState);
				case TangentRotating -> gen.state(BlockStateGenerator::tangentRotating);
				case RandomRotation -> gen.state(BlockStateModelGenerator::createBlockStateWithRandomHorizontalRotations);
				case RandomMirror ->
				{
					var id = AssetUtils.getTextureName(block);
					var mirrored = id.withSuffixedPath("_mirrored");
					gen.state((b, modelId) -> BlockStateModelGenerator.createBlockStateWithTwoModelAndRandomInversion(b, id, mirrored));
				}
				default -> throw new RuntimeException("Unsupported value " + a.state());
			}

			switch (a.model())
			{
				case None ->
				{
				}
				case Cube -> gen.model(ModelFile::cube);
				case CubeNoCull -> gen.model(ModelFile::cube_no_cull);
				case Leaves -> gen.model(ModelFile::leaves);
				case Fan -> gen.model(ModelFile::fan);
				case ColumnTop -> gen.model(ModelFile::columnTop);
				case ColumnTopBottom -> gen.model(ModelFile::columnTopBottom);
				case Cross -> gen.model(ModelFile::cross);
				case RandomMirror -> gen.models(ModelFile::randomMirror);
				default -> throw new RuntimeException("Unsupported value " + a.model());
			}

			switch (a.itemModel())
			{
				case None ->
				{
				}
				case BlockItem ->
				{
					if (a.model() != TrModel.None)
						gen.itemModel(ModelFile::ofBlock);
				}
				case Sprite -> gen.itemModel(ModelFile::item);
				case SeparateSprite -> gen.itemModel(ModelFile::blockSeparateItem);
				default -> throw new RuntimeException("Unsupported value " + a.itemModel());
			}

			switch (a.loot())
			{
				case SingleSelf -> gen.lootTable(LootTableFile::singleSelf);
				case MultiOnlyCenter -> gen.lootTable(LootTableFile::multiBlockOnlyCenter);
				case Door -> gen.lootTable(LootTableFile::door);
				case Pickling -> gen.lootTable(block1 -> LootTableFile.pickling((Block & IPicklingBlock)block1));
				default -> throw new RuntimeException("Unsupported value " + a.loot());
			}

			for (var tag : a.tags())
				gen.blockTag(getTagKey(tag));

			for (var tag : a.itemTags())
				gen.itemTag(getTagKey(tag));

			gen.build(assets);
		});
	}
}
