package com.parzivail.datagen.tarkin;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.util.Lumberjack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * T.A.R.K.I.N. - Text Asset Record-Keeping, Integration, and Normalization
 */
public class Tarkin
{
	public static void main(String arg)
	{
		Lumberjack.setLogHeader("TARKIN");

//		AssetGenerator.setOutputRoot(Paths.get("..", "TEMP", "src", "main", "resources"));

		List<BuiltAsset> assets = new ArrayList<>();

		generateBlocks(assets);

		// TODO: support tags instead of items in recipes
		// TODO: support substitutes instead of items in recipes

		recipeFood(assets, SwgItems.Food.BanthaChop, SwgItems.Food.BanthaSteak);
		recipeFood(assets, SwgItems.Food.GizkaChop, SwgItems.Food.GizkaSteak);
		recipeFood(assets, SwgItems.Food.MynockWing, SwgItems.Food.FriedMynockWing);
		recipeFood(assets, SwgItems.Food.NerfChop, SwgItems.Food.NerfSteak);
		recipeFood(assets, SwgItems.Food.QrikkiBread, SwgItems.Food.QrikkiWaffle);

		mineralRecipeSet(
				assets,
				SwgBlocks.Ore.Beskar,
				SwgItems.Ingot.Beskar,
				SwgBlocks.MaterialBlock.Beskar
		);

		mineralRecipeSet(
				assets,
				SwgBlocks.Ore.Chromium,
				SwgItems.Ingot.Chromium,
				SwgItems.Nugget.Chromium,
				SwgBlocks.MaterialBlock.Chromium
		);

		mineralRecipeSet(
				assets,
				SwgBlocks.Ore.Cortosis,
				SwgItems.Ingot.Cortosis,
				SwgBlocks.MaterialBlock.Cortosis
		);

		mineralRecipeSet(
				assets,
				SwgBlocks.Ore.Desh,
				SwgItems.Ingot.Desh,
				SwgItems.Nugget.Desh,
				SwgBlocks.MaterialBlock.Desh
		);

		mineralRecipeSet(
				assets,
				SwgBlocks.Ore.Diatium,
				SwgItems.Ingot.Diatium,
				SwgItems.Nugget.Diatium,
				SwgBlocks.MaterialBlock.Diatium
		);

		// TODO: durasteel

		mineralRecipeSet(
				assets,
				SwgBlocks.Ore.Lommite,
				SwgItems.Ingot.Lommite,
				// TODO: nugget
				SwgBlocks.MaterialBlock.Lommite
		);

		// TODO: plasteel

		mineralRecipeSet(
				assets,
				SwgBlocks.Ore.Titanium,
				SwgItems.Ingot.Titanium,
				SwgItems.Nugget.Titanium,
				SwgBlocks.MaterialBlock.Titanium
		);

		mineralRecipeSet(
				assets,
				SwgBlocks.Ore.Zersium,
				SwgItems.Ingot.Zersium,
				SwgItems.Nugget.Zersium,
				SwgBlocks.MaterialBlock.Zersium
		);

		for (BuiltAsset asset : assets)
		{
			Lumberjack.log("Wrote %s", asset.getFilename());
			asset.write();
		}
	}

	private static void recipeFood(List<BuiltAsset> assets, Item input, Item output)
	{
		RecipeGenerator.Cooking.smelting(output, input, "raw")
		                       .build(assets);
		RecipeGenerator.Cooking.smoking(output, input, "smoking_raw")
		                       .build(assets);
		RecipeGenerator.Cooking.campfire(output, input, "campfire_raw")
		                       .build(assets);
	}

	private static void mineralRecipeSet(List<BuiltAsset> assets, ItemConvertible ore, ItemConvertible ingot, ItemConvertible block)
	{
		recipeOreToIngot(assets, ore, ingot);
		recipeInterchange(assets, ingot, "ingot", block, "block");
	}

	private static void mineralRecipeSet(List<BuiltAsset> assets, ItemConvertible ore, ItemConvertible ingot, ItemConvertible nugget, ItemConvertible block)
	{
		recipeOreToIngot(assets, ore, ingot);
		recipeInterchange(assets, ingot, "ingot", block, "block");
		recipeInterchange(assets, nugget, "nugget", ingot, "ingot");
	}

	private static void recipeInterchange(List<BuiltAsset> assets, ItemConvertible smallUnit, String smallUnitName, ItemConvertible largeUnit, String largeUnitName)
	{
		RecipeGenerator.Shapeless.of(new ItemStack(smallUnit, 9), largeUnitName)
		                         .ingredient(largeUnit)
		                         .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(largeUnit, 1))
		                      .grid3x3Fill(smallUnit, smallUnitName)
		                      .build(assets);
	}

	private static void recipeOreToIngot(List<BuiltAsset> assets, ItemConvertible ore, ItemConvertible ingot)
	{
		RecipeGenerator.Cooking.smelting(ingot, ore, "ore")
		                       .build(assets);
		RecipeGenerator.Cooking.blasting(ingot, ore, "blasting_ore")
		                       .build(assets);
	}

	private static void generateBlocks(List<BuiltAsset> assets)
	{
		BlockGenerator.leaves(SwgBlocks.Leaves.Sequoia).build(assets);

		BlockGenerator.column(SwgBlocks.Log.Sequoia, Resources.identifier("block/log_sequoia_top"), Resources.identifier("block/log_sequoia_side")).build(assets);

		BlockGenerator.basic(SwgBlocks.Ore.Beskar).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Chromium).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Cortosis).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Desh).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Diatium).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Exonium).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Helicite).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Ionite).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Kelerium).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Lommite).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Rubindum).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Thorolide).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Titanium).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Zersium).build(assets);

		BlockGenerator.basic(SwgBlocks.MaterialBlock.Beskar).build(assets);
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Chromium).build(assets);
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Cortosis).build(assets);
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Desh).build(assets);
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Diatium).build(assets);
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Durasteel).build(assets);
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Lommite).build(assets);
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Plasteel).build(assets);
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Titanium).build(assets);
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Zersium).build(assets);

		BlockGenerator.cross(SwgBlocks.Plant.FunnelFlower).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.BlossomingFunnelFlower).build(assets);

		BlockGenerator.basic(SwgBlocks.Panel.ImperialBase).build(assets);
		BlockGenerator.basic(SwgBlocks.Panel.ImperialBlackBase).build(assets);

		BlockGenerator.column(SwgBlocks.Panel.Imperial1, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_1")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.Imperial2, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_2")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.Imperial3, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_3")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLight1, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_1")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLight2, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_2")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLight3, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_3")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLight4, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_4")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLight5, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_5")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLight6, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_6")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLightTall1, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_tall_1")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLightTall2, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_tall_2")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLightTall3, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_tall_3")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLightTall4, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_tall_4")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLightDecoy, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_decoy")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialLightOff, Resources.identifier("block/panel_imperial_base"), Resources.identifier("block/panel_imperial_light_off")).build(assets);

		BlockGenerator.basic(SwgBlocks.Panel.LabWall).build(assets);

		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.Tatooine).build(assets);
		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.DenseTatooine).build(assets);
		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.TatooineCanyon).build(assets);

		BlockGenerator.basic(SwgBlocks.Stone.MassassiBricks).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.MassassiSmooth).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.MassassiBrickStairs, Resources.identifier("block/stone_massassi_bricks")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.MassassiSmoothStairs, Resources.identifier("block/stone_massassi_smooth")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.MassassiBrickSlab, Resources.identifier("block/stone_massassi_bricks")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.MassassiSmoothSlab, Resources.identifier("block/stone_massassi_smooth")).build(assets);

		BlockGenerator.basic(SwgBlocks.Stone.Pourstone).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.LightPourstone).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.PourstoneStairs, Resources.identifier("block/pourstone")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.LightPourstoneStairs, Resources.identifier("block/light_pourstone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.PourstoneSlab, Resources.identifier("block/pourstone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.LightPourstoneSlab, Resources.identifier("block/light_pourstone")).build(assets);

		BlockGenerator.basic(SwgBlocks.Stone.Ilum).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.IlumSlab, Resources.identifier("block/ilum_stone")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.IlumStairs, Resources.identifier("block/ilum_stone")).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.IlumSmooth).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.IlumSmoothSlab, Resources.identifier("block/smooth_ilum_stone"), Resources.identifier("block/smooth_ilum_stone"), Resources.identifier("block/smooth_ilum_stone_slab_side")).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.IlumBricks).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.IlumBrickSlab, Resources.identifier("block/ilum_stone_bricks")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.IlumBrickStairs, Resources.identifier("block/ilum_stone_bricks")).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.IlumChiseledBricks).build(assets);

		BlockGenerator.staticColumn(SwgBlocks.Vent.Tatooine, Resources.identifier("block/tatooine_vent_top"), Resources.identifier("block/tatooine_vent_side")).build(assets);

		BlockGenerator.basic(SwgBlocks.Workbench.Blaster).build(assets);
		BlockGenerator.basic(SwgBlocks.Workbench.Lightsaber).build(assets);
	}
}
