package dev.pswg.datagen;

import dev.pswg.Gadgets;
import dev.pswg.Galaxies;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

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
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator)
		{
			register(itemModelGenerator, Gadgets.THERMAL_DETONATOR_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
			register(itemModelGenerator, Gadgets.FRAGMENTATION_GRENADE_ITEM, Galaxies.id("item/wizard"), Models.GENERATED);
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
			translationBuilder.add(Gadgets.THERMAL_DETONATOR_ITEM, "Thermal Detonator");
			translationBuilder.add(Gadgets.FRAGMENTATION_GRENADE_ITEM, "Fragmentation Grenade");

			translationBuilder.add(Gadgets.FRAGMENTATION_GRENADE_DESTROY, "Fragmenetation Grenade Destroy");
			translationBuilder.add(Gadgets.DETONATES_GRENADE, "Detonates Grenade");
			translationBuilder.add(Gadgets.GRENADES_TAG, "Grenades");

			translationBuilder.add("subtitle.pswg_gadgets.grenade_throw", "Grenade Thrown");
			translationBuilder.add("subtitle.pswg_gadgets.grenade_arm", "Grenade Armed");
			translationBuilder.add("subtitle.pswg_gadgets.grenade_disarm", "Grenade Disarmed");
			translationBuilder.add("subtitle.pswg_gadgets.fragmentationgrenade.explode", "C25 Grenade Explosion");
			translationBuilder.add("subtitle.pswg_gadgets.thermaldetonator.explode", "Thermal Detonator Explosion");

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
			getOrCreateTagBuilder(Gadgets.GRENADES_TAG)
					.add(Gadgets.THERMAL_DETONATOR_ITEM)
					.add(Gadgets.FRAGMENTATION_GRENADE_ITEM);
		}
	}
	/**
	 * The gadget block tag generator. All block tags should be added
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
			getOrCreateTagBuilder(Gadgets.FRAGMENTATION_GRENADE_DESTROY)
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

			getOrCreateTagBuilder(Gadgets.DETONATES_GRENADE)
					.add(Blocks.REDSTONE_BLOCK)
					.add(Blocks.REDSTONE_TORCH)
					.add(Blocks.REDSTONE_WALL_TORCH)
					.add(Blocks.FIRE)
					.add(Blocks.SOUL_FIRE);
		}
	}
}
