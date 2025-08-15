package dev.pswg.datagen;

import dev.pswg.Gadgets;
import dev.pswg.block.DyedBlocks;
import dev.pswg.block.DyedStoneProducts;
import dev.pswg.block.StoneProducts;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsItems;
import dev.pswg.Galaxies;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The gadget data generator
 */
public class GadgetsDataGenerator implements DataGeneratorEntrypoint
{
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator)
	{
		var pack = generator.createPack();

		pack.addProvider(LangGenerator::new);
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
		pack.addProvider(ModelGenerator::new);
	}

	/**
	 * The gadget model generator. All models should be added through
	 * this generator.
	 */
	private static class ModelGenerator extends GalaxiesModelProvider
	{
		public ModelGenerator(FabricDataOutput output)
		{
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
		{
			DataGenUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, Block.class, (genBlock, dataGenBlock) -> {

				switch (dataGenBlock.model())
				{
					case CubeAll -> blockStateModelGenerator.registerSimpleCubeAll(genBlock);
					case Column -> blockStateModelGenerator.registerSingleton(genBlock, TexturedModel.CUBE_COLUMN);
					case Cross -> blockStateModelGenerator.registerTintableCross(genBlock, BlockStateModelGenerator.CrossType.NOT_TINTED);
					case DataGenModel ->
					{
						switch (dataGenBlock.dataGenModelKey())
						{
							case "corrugated_crate":
								registerCorrugatedCrate(blockStateModelGenerator, genBlock);
							case null, default:
						}
					}
				}
			});
			DataGenUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, DyedBlocks.class, (genDyedBlocks, dataGenBlock) -> {

				for (Block block : genDyedBlocks.values())
					switch (dataGenBlock.model())
					{
						case CubeAll -> blockStateModelGenerator.registerSimpleCubeAll(block);
						case Column -> blockStateModelGenerator.registerSingleton(block, TexturedModel.CUBE_COLUMN);
						case Cross -> blockStateModelGenerator.registerTintableCross(block, BlockStateModelGenerator.CrossType.NOT_TINTED);
						case DataGenModel ->
						{
							switch (dataGenBlock.dataGenModelKey())
							{
								case "corrugated_crate":
									registerCorrugatedCrate(blockStateModelGenerator, block);
								case null, default:
							}
						}
					}
			});
			DataGenUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, StoneProducts.class, (stoneProducts, dataGenBlock) -> {

				if (dataGenBlock.model() != DataGenBlockModel.None)
				{
					blockStateModelGenerator.registerCubeAllModelTexturePool(stoneProducts.block)
					                        .wall(stoneProducts.wall)
					                        .slab(stoneProducts.slab)
					                        .stairs(stoneProducts.stairs);
				}
			});
			DataGenUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, DyedStoneProducts.class, (dyedStoneProducts, dataGenBlock) -> {

				if (dataGenBlock.model() != DataGenBlockModel.None)
				{
					for (StoneProducts stoneProducts : dyedStoneProducts.values())
						blockStateModelGenerator.registerCubeAllModelTexturePool(stoneProducts.block)
						                        .wall(stoneProducts.wall)
						                        .slab(stoneProducts.slab)
						                        .stairs(stoneProducts.stairs);
				}
			});

			//String blockKey = genBlock.getRegistryEntry().getKey().get().getValue().toString().substring(Gadgets.MODID.length()+1);
			//TexturedModel.makeFactory(block -> TextureMap.texture(Gadgets.id("block/"+blockKey)), blockModel("corrugated_crate", TextureKey.of(blockKey)));

		}

		private static Model blockModel(String parent, TextureKey... requiredTextureKeys)
		{
			return new Model(Optional.of(Gadgets.id("block/" + parent)), Optional.empty(), requiredTextureKeys);
		}

		public final void registerCorrugatedCrate(BlockStateModelGenerator generator, Block block)
		{
			var crateKey = getCorrugatedCrateKey(block).withPrefixedPath("block/model/corrugated_crate/");
			TexturedModel.makeFactory(block1 -> TextureMap.all(crateKey).put(TextureKey.PARTICLE, crateKey.withSuffixedPath("_particle")), blockModel("template_corrugated_crate", TextureKey.ALL, TextureKey.PARTICLE)).upload(block, generator.modelCollector);
			generator.registerSimpleState(block);
		}

		public static Identifier getBlockKey(Block block)
		{
			return block.getRegistryEntry().getKey().get().getValue();
		}

		public static Identifier getCorrugatedCrateKey(Block block)
		{
			String string = block.getRegistryEntry().getKey().get().getValue().toString();
			return Identifier.of(string.substring(0, string.indexOf("_corrugated_crate")));
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator)
		{

			register(itemModelGenerator, GadgetsItems.THERMAL_DETONATOR_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
			register(itemModelGenerator, GadgetsItems.FRAGMENTATION_GRENADE_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
			register(itemModelGenerator, GadgetsItems.NERVE_GAS_GRENADE_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
			register(itemModelGenerator, GadgetsItems.SMOKE_SIGNAL_GRENADE_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
			register(itemModelGenerator, GadgetsItems.IMPACT_GRENADE_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
			register(itemModelGenerator, GadgetsItems.INFERNO_GRENADE_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);

			register(itemModelGenerator, GadgetsItems.PRESSURE_MINE_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
			register(itemModelGenerator, GadgetsItems.TRIPWIRE_MINE_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);

			register(itemModelGenerator, GadgetsItems.SCRAP_ITEM, Gadgets.id("item/scrap"), Models.GENERATED);

			register(itemModelGenerator, GadgetsItems.CUTTER_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
			register(itemModelGenerator, GadgetsItems.SPANNER_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
			register(itemModelGenerator, GadgetsItems.CALIBRATOR_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
		}
	}

	/**
	 * The gadget language file generator. All language entries should be
	 * added through this generator.
	 */
	private static class LangGenerator extends FabricLanguageProvider
	{
		protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup)
		{
			super(dataOutput, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder)
		{
			translationBuilder.add(GadgetsItems.THERMAL_DETONATOR_ITEM, "CLS-A Thermal Detonator");
			translationBuilder.add(GadgetsItems.FRAGMENTATION_GRENADE_ITEM, "C-25 Fragmentation Grenade");
			translationBuilder.add(GadgetsItems.NERVE_GAS_GRENADE_ITEM, "FEX-M3 Gas Grenade");
			translationBuilder.add(GadgetsItems.SMOKE_SIGNAL_GRENADE_ITEM, "NACHT-5 Smoke Grenade");
			translationBuilder.add(GadgetsItems.IMPACT_GRENADE_ITEM, "Impact Grenade");
			translationBuilder.add(GadgetsItems.INFERNO_GRENADE_ITEM, "D-24 Inferno Grenade");
			translationBuilder.add(GadgetsItems.PRESSURE_MINE_ITEM, "Pressure Mine");
			translationBuilder.add(GadgetsItems.TRIPWIRE_MINE_ITEM, "Tripwire Mine");

			DataGenUtil.consumeAnnotatedFields(DataGenBlock.class, GadgetsBlocks.class, Block.class, (block, dataGenBlock) -> {

				if (!Objects.equals(dataGenBlock.langOverride(), ""))
				{
					translationBuilder.add(block, dataGenBlock.langOverride());
				}
				else
				{
					translationBuilder.add(block, generateDefaultLang(block.getRegistryEntry().registryKey().getValue()));
				}
			});
			//translationBuilder.add(GadgetsBlocks.CHARRED_BLOCK, "Charred Wood");
			//translationBuilder.add(GadgetsBlocks.FERTILE_DIRT_BLOCK, "Fertile Dirt");

			translationBuilder.add(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY, "Fragmenetation Grenade Destroy");
			translationBuilder.add(GadgetsBlocks.Tags.DETONATES_GRENADE, "Detonates Grenade");
			translationBuilder.add(GadgetsBlocks.Tags.BOUNCY, "Bouncy");
			translationBuilder.add(GadgetsBlocks.Tags.INFERNO_CHAR, "Inferno Grenade Char");
			translationBuilder.add(GadgetsBlocks.Tags.INFERNO_DESTROY, "Inferno Grenade Destroy");
			translationBuilder.add(GadgetsBlocks.Tags.GAS_PASS_THROUGH, "Gas Pass Through");
			translationBuilder.add(GadgetsItems.Tags.GRENADES_TAG, "Grenades");

			translationBuilder.add("subtitle.pswg_gadgets.grenade_throw", "Grenade Thrown");
			translationBuilder.add("subtitle.pswg_gadgets.grenade_arm", "Grenade Armed");
			translationBuilder.add("subtitle.pswg_gadgets.grenade_disarm", "Grenade Disarmed");
			translationBuilder.add("subtitle.pswg_gadgets.fragmentationgrenade.explode", "C25 Grenade Explosion");
			translationBuilder.add("subtitle.pswg_gadgets.thermaldetonator.explode", "Thermal Detonator Explosion");

			translationBuilder.add("effect.pswg_gadgets.intoxicated", "Intoxicated");

		}
	}

	/**
	 * The gadget item tag generator. All item tags should be added
	 * through this generator.
	 */
	private static class ItemTagGenerator extends FabricTagProvider.ItemTagProvider
	{
		public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture)
		{
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
		{
			getOrCreateTagBuilder(GadgetsItems.Tags.GRENADES_TAG)
					.add(GadgetsItems.THERMAL_DETONATOR_ITEM)
					.add(GadgetsItems.FRAGMENTATION_GRENADE_ITEM)
					.add(GadgetsItems.NERVE_GAS_GRENADE_ITEM)
					.add(GadgetsItems.SMOKE_SIGNAL_GRENADE_ITEM)
					.add(GadgetsItems.IMPACT_GRENADE_ITEM)
					.add(GadgetsItems.INFERNO_GRENADE_ITEM);

			getOrCreateTagBuilder(GadgetsItems.Tags.MINES_TAG)
					.add(GadgetsItems.PRESSURE_MINE_ITEM)
					.add(GadgetsItems.TRIPWIRE_MINE_ITEM);
		}
	}
	/**
	 * The gadget connectingBlock tag generator. All connectingBlock tags should be added
	 * through this generator.
	 */
	private static class BlockTagGenerator extends FabricTagProvider.BlockTagProvider
	{
		public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture)
		{
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
		{
			getOrCreateTagBuilder(GadgetsBlocks.Tags.FRAGMENTATION_GRENADE_DESTROY)
					.addOptionalTag(BlockTags.LEAVES)
					.addOptionalTag(BlockTags.CAVE_VINES)
					.addOptionalTag(BlockTags.CROPS)
					.addOptionalTag(BlockTags.FLOWERS)
					.addOptionalTag(BlockTags.SAPLINGS)
					.addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS)
					.addOptionalTag(BlockTags.ICE)
					.add(Blocks.FERN)
					.add(Blocks.LARGE_FERN)
					.add(Blocks.BROWN_MUSHROOM)
					.add(Blocks.RED_MUSHROOM)
					.add(Blocks.DEAD_BUSH)
					.add(Blocks.SHORT_GRASS)
					.add(Blocks.TALL_GRASS)
					.add(Blocks.SNOW);

			getOrCreateTagBuilder(GadgetsBlocks.Tags.DETONATES_GRENADE)
					.add(Blocks.REDSTONE_BLOCK)
					.add(Blocks.REDSTONE_TORCH)
					.add(Blocks.REDSTONE_WALL_TORCH)
					.add(Blocks.FIRE)
					.add(Blocks.SOUL_FIRE);

			getOrCreateTagBuilder(GadgetsBlocks.Tags.BOUNCY)
					.add(Blocks.HONEY_BLOCK)
					.add(Blocks.SLIME_BLOCK);

			getOrCreateTagBuilder(GadgetsBlocks.Tags.GAS_PASS_THROUGH)
					.addOptionalTag(BlockTags.LEAVES)
					.add(Blocks.COPPER_GRATE);

			getOrCreateTagBuilder(GadgetsBlocks.Tags.INFERNO_CHAR)
					.add(Blocks.MOSS_BLOCK)
					.addOptionalTag(BlockTags.LOGS)
					.addOptionalTag(BlockTags.PLANKS)
					.addOptionalTag(BlockTags.BAMBOO_BLOCKS)
					.addOptionalTag(BlockTags.WOOL)
					.addOptionalTag(BlockTags.WOODEN_FENCES)
					.addOptionalTag(BlockTags.WOODEN_SLABS)
					.addOptionalTag(BlockTags.WOODEN_STAIRS)
					.addOptionalTag(BlockTags.WOODEN_TRAPDOORS)
					.addOptionalTag(ConventionalBlockTags.BOOKSHELVES)
			;

			getOrCreateTagBuilder(GadgetsBlocks.Tags.INFERNO_DESTROY)
					.addOptionalTag(BlockTags.LEAVES)
					.addOptionalTag(BlockTags.CAVE_VINES)
					.addOptionalTag(BlockTags.FLOWERS)
					.addOptionalTag(BlockTags.CROPS)
					.addOptionalTag(BlockTags.CRIMSON_STEMS)
					.addOptionalTag(BlockTags.ALL_SIGNS)
					.addOptionalTag(BlockTags.BANNERS)
					.addOptionalTag(BlockTags.FLOWER_POTS)
					.addOptionalTag(BlockTags.WOOL_CARPETS)
					.addOptionalTag(BlockTags.WOODEN_BUTTONS)
					.addOptionalTag(BlockTags.WARPED_STEMS)
					.addOptionalTag(BlockTags.SNOW)
					.addOptionalTag(BlockTags.ICE)
					.add(Blocks.BAMBOO)
					.add(Blocks.VINE)
					.add(Blocks.FERN)
					.add(Blocks.LARGE_FERN)
					.add(Blocks.DEAD_BUSH)
					.add(Blocks.TALL_GRASS)
					.add(Blocks.SHORT_GRASS)
					.add(Blocks.CACTUS)
			;



		}
	}
	private static String generateDefaultLang(Identifier reg)
	{
		var path = reg.getPath();
		return Arrays.stream(path.split("_"))
		             .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
		             .collect(Collectors.joining(" "));
	}
}
