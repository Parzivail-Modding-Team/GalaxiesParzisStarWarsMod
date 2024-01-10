package com.parzivail.datagen.tarkin;

import com.parzivail.datagen.tarkin.config.PswgTarkin;
import com.parzivail.datagen.tarkin.config.TcwTarkin;
import com.parzivail.pswg.container.SwgTags;
import com.parzivail.tarkin.api.*;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.block.IPicklingBlock;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * T.A.R.K.I.N. - Text Asset Record-Keeping, Integration, and Normalization
 */
public class Tarkin
{
	public static final Lumberjack LOG = new Lumberjack("TARKIN");

	public static void main() throws Exception
	{
		AssetGenerator.setOutputRoot(Path.of(System.getenv("TARKIN_OUT_DIR")));

		List<BuiltAsset> assets = new ArrayList<>();

		var tarkinModid = System.getProperty("tarkin", "");

		switch (tarkinModid)
		{
			case "pswg" -> PswgTarkin.build(assets);
			case "pswg_addon_clonewars" -> TcwTarkin.build(assets);
		}

		BuiltAsset.nukeRecipeDir();
		BuiltAsset.nukeBlockstateDir();
		BuiltAsset.nukeBlockModelJsons();
		BuiltAsset.nukeItemModelJsons();
		BuiltAsset.nukeBlockLootTables();
		BuiltAsset.nukeTags();

		for (var asset : assets)
		{
			LOG.log("Wrote %s", asset.getFilename());
			asset.write();
		}

		LOG.log("Wrote %s assets", assets.size());

		// Synchronize the keys of the en_us locale
		BuiltAsset.mergeLanguageKeys(new Identifier(tarkinModid, LanguageProvider.OUTPUT_LOCALE), new Identifier(tarkinModid, LanguageProvider.TARGET_LOCALE));
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

	public static <T, TA extends Annotation> void consumeFields(Class<TA> annotationClazz, Class<?> rootClazz, Class<T> registryType, BiConsumer<T, TA> consumer)
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
			consumeFields(annotationClazz, clazz, registryType, consumer);
	}

	public static void registerLangFields(Class<?> rootClazz, LanguageBuilder languageBuilder, List<BuiltAsset> assets)
	{
		consumeFields(TarkinLang.class, rootClazz, String.class, (s, a) -> languageBuilder.entry(s).build(assets));
	}

	private static TagKey<Item> getTagKey(TrItemTag preset)
	{
		return switch (preset)
				{
					case TrinketsChestBack -> TagKey.of(RegistryKeys.ITEM, new Identifier("trinkets", "chest/back"));
					case Leaves -> ItemTags.LEAVES;
					case DesertSand -> SwgTags.Items.DESERT_SAND;
					case DesertSandstone -> SwgTags.Items.DESERT_SANDSTONE;
					default -> throw new RuntimeException("Unsupported value " + preset);
				};
	}

	private static TagKey<Block> getTagKey(TrBlockTag preset)
	{
		return switch (preset)
		{
			case PickaxeMineable -> BlockTags.PICKAXE_MINEABLE;
			case ShearsMineable -> FabricMineableTags.SHEARS_MINEABLE;
			case SlidingDoor -> SwgTags.Blocks.SLIDING_DOORS;
			case DesertSand -> SwgTags.Blocks.DESERT_SAND;
			case DesertSandstone -> SwgTags.Blocks.DESERT_SANDSTONE;
			case Leaves -> BlockTags.LEAVES;
			case DeadBushSubstrate -> BlockTags.DEAD_BUSH_MAY_PLACE_ON;
			case BlasterDestroy -> SwgTags.Blocks.BLASTER_DESTROY;
			case BlasterExplode -> SwgTags.Blocks.BLASTER_EXPLODE;
			case BlasterReflect -> SwgTags.Blocks.BLASTER_REFLECT;
			default -> throw new RuntimeException("Unsupported value " + preset);
		};
	}

	public static void registerItemFields(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeFields(TarkinItem.class, rootClazz, Item.class, (item, a) -> {
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
				case Item -> gen.model(ModelFile::item);
				case SpawnEgg -> gen.model(ModelFile::spawn_egg);
				case HandheldItem -> gen.model(ModelFile::handheld_item);
				default -> throw new RuntimeException("Unsupported value " + a.model());
			}

			gen.build(assets);
		});
	}

	public static void registerBlockFields(Class<?> rootClazz, List<BuiltAsset> assets)
	{
		consumeFields(TarkinBlock.class, rootClazz, Block.class, (block, a) -> {
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
				case Singleton -> gen.state(BlockStateModelGenerator::createSingletonBlockState);
				case TangentRotating -> gen.state(BlockStateGenerator::tangentRotating);
				case RandomRotation -> gen.state(BlockStateModelGenerator::createBlockStateWithRandomHorizontalRotations);
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
				default -> throw new RuntimeException("Unsupported value " + a.model());
			}

			switch (a.itemModel())
			{
				case None ->
				{
				}
				case Block -> gen.itemModel(ModelFile::ofBlock);
				case Item -> gen.itemModel(ModelFile::item);
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
