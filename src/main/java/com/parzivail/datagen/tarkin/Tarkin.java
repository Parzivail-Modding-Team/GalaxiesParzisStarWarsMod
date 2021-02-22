package com.parzivail.datagen.tarkin;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.util.Lumberjack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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
		generateItems(assets);
		generateRecipes(assets);

		for (BuiltAsset asset : assets)
		{
			Lumberjack.log("Wrote %s", asset.getFilename());
			asset.write();
		}
	}

	private static void generateRecipes(List<BuiltAsset> assets)
	{
		// TODO: support tags instead of items in recipes
		// TODO: support substitutes instead of items in recipes

		RecipeGenerator.buildFood(assets, SwgItems.Food.BanthaChop, SwgItems.Food.BanthaSteak);
		RecipeGenerator.buildFood(assets, SwgItems.Food.GizkaChop, SwgItems.Food.GizkaSteak);
		RecipeGenerator.buildFood(assets, SwgItems.Food.MynockWing, SwgItems.Food.FriedMynockWing);
		RecipeGenerator.buildFood(assets, SwgItems.Food.NerfChop, SwgItems.Food.NerfSteak);
		RecipeGenerator.buildFood(assets, SwgItems.Food.QrikkiBread, SwgItems.Food.QrikkiWaffle);

		RecipeGenerator.buildMetal(
				assets,
				SwgBlocks.Ore.Beskar,
				SwgItems.Ingot.Beskar,
				SwgBlocks.MaterialBlock.Beskar
		);

		RecipeGenerator.buildMetal(
				assets,
				SwgBlocks.Ore.Chromium,
				SwgItems.Ingot.Chromium,
				SwgItems.Nugget.Chromium,
				SwgBlocks.MaterialBlock.Chromium
		);

		RecipeGenerator.buildMetal(
				assets,
				SwgBlocks.Ore.Cortosis,
				SwgItems.Ingot.Cortosis,
				SwgBlocks.MaterialBlock.Cortosis
		);

		RecipeGenerator.buildMetal(
				assets,
				SwgBlocks.Ore.Desh,
				SwgItems.Ingot.Desh,
				SwgItems.Nugget.Desh,
				SwgBlocks.MaterialBlock.Desh
		);

		RecipeGenerator.buildMetal(
				assets,
				SwgBlocks.Ore.Diatium,
				SwgItems.Ingot.Diatium,
				SwgItems.Nugget.Diatium,
				SwgBlocks.MaterialBlock.Diatium
		);

		// TODO: durasteel

		RecipeGenerator.buildMetal(
				assets,
				SwgBlocks.Ore.Lommite,
				SwgItems.Ingot.Lommite,
				// TODO: nugget
				SwgBlocks.MaterialBlock.Lommite
		);

		// TODO: plasteel

		RecipeGenerator.buildMetal(
				assets,
				SwgBlocks.Ore.Titanium,
				SwgItems.Ingot.Titanium,
				SwgItems.Nugget.Titanium,
				SwgBlocks.MaterialBlock.Titanium
		);

		RecipeGenerator.buildMetal(
				assets,
				SwgBlocks.Ore.Zersium,
				SwgItems.Ingot.Zersium,
				SwgItems.Nugget.Zersium,
				SwgBlocks.MaterialBlock.Zersium
		);

		RecipeGenerator.Shapeless.of(new ItemStack(SwgItems.Dust.Ionite), "crystal")
		                         .ingredient(SwgItems.Crystal.Ionite)
		                         .build(assets);

		RecipeGenerator.Shapeless.of(new ItemStack(Items.BLUE_DYE), "ionite")
		                         .ingredient(SwgItems.Dust.Ionite)
		                         .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.TNT))
		                      .grid3x3("ionite", SwgItems.Dust.Ionite, SwgBlocks.Sand.Tatooine, SwgItems.Dust.Ionite,
		                               SwgBlocks.Sand.Tatooine, SwgItems.Dust.Ionite, SwgBlocks.Sand.Tatooine,
		                               SwgItems.Dust.Ionite, SwgBlocks.Sand.Tatooine, SwgItems.Dust.Ionite)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.MaterialBlock.Ionite))
		                      .fill3x3("dust", SwgItems.Dust.Ionite)
		                      .build(assets);

		RecipeGenerator.Shapeless.of(new ItemStack(SwgItems.Dust.Ionite, 9), "block")
		                         .ingredient(SwgBlocks.MaterialBlock.Ionite)
		                         .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.ZersiumRod, 3))
		                      .grid3x3("zersium", null, SwgItems.Ingot.Zersium, null,
		                               null, SwgItems.Ingot.Zersium, null,
		                               null, SwgItems.Ingot.Zersium, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Pickaxe.Beskar))
		                      .grid3x3("crafting", SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar,
		                               null, SwgItems.CraftingComponents.ZersiumRod, null,
		                               null, SwgItems.CraftingComponents.ZersiumRod, null)
		                      .build(assets);
	}

	private static void generateItems(List<BuiltAsset> assets)
	{
		ItemGenerator.basic(SwgItems.Axe.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Axe.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Axe.Beskar).build(assets);

		ItemGenerator.empty(SwgItems.Blaster.Blaster).build(assets);

		ItemGenerator.basic(SwgItems.CraftingComponents.ElectricMotor).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.Led).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.Turbine).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.BallBearing).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.DeshWire).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.ZersiumRod).build(assets);

		ItemGenerator.basic(SwgItems.Crystal.Ionite).build(assets);

		ItemGenerator.basic(SwgItems.Debug.Debug).build(assets);

		ItemGenerator.basic(SwgItems.Dust.Ionite).build(assets);

		ItemGenerator.basic(SwgItems.Food.SaltPile).build(assets);
		ItemGenerator.basic(SwgItems.Food.JoganFruit).build(assets);
		ItemGenerator.basic(SwgItems.Food.Chasuka).build(assets);
		ItemGenerator.basic(SwgItems.Food.Meiloorun).build(assets);
		ItemGenerator.basic(SwgItems.Food.MynockWing).build(assets);
		ItemGenerator.basic(SwgItems.Food.FriedMynockWing).build(assets);
		ItemGenerator.basic(SwgItems.Food.BanthaChop).build(assets);
		ItemGenerator.basic(SwgItems.Food.BanthaSteak).build(assets);
		ItemGenerator.basic(SwgItems.Food.NerfChop).build(assets);
		ItemGenerator.basic(SwgItems.Food.NerfSteak).build(assets);
		ItemGenerator.basic(SwgItems.Food.GizkaChop).build(assets);
		ItemGenerator.basic(SwgItems.Food.GizkaSteak).build(assets);

		ItemGenerator.basic(SwgItems.Food.FlangthTakeout).build(assets);
		ItemGenerator.basic(SwgItems.Food.FlangthPlate).build(assets);

		ItemGenerator.basic(SwgItems.Food.DeathStickRed).build(assets);
		ItemGenerator.basic(SwgItems.Food.DeathStickYellow).build(assets);

		ItemGenerator.basic(SwgItems.Food.BlueMilk).build(assets);
		ItemGenerator.basic(SwgItems.Food.BluePuffCube).build(assets);
		ItemGenerator.basic(SwgItems.Food.BlueYogurt).build(assets);

		ItemGenerator.basic(SwgItems.Food.QrikkiBread).build(assets);
		ItemGenerator.basic(SwgItems.Food.QrikkiWaffle).build(assets);

		ItemGenerator.basic(SwgItems.Hoe.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Hoe.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Hoe.Beskar).build(assets);

		ItemGenerator.basic(SwgItems.Ingot.Beskar).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Chromium).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Cortosis).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Desh).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Diatium).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Lommite).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Plasteel).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Transparisteel).build(assets);
		ItemGenerator.basic(SwgItems.Ingot.Zersium).build(assets);

		ItemGenerator.empty(SwgItems.Lightsaber.Lightsaber).build(assets);

		// TODO: ItemGenerator.basic(SwgItems.Nugget.Beskar).build(assets);
		ItemGenerator.basic(SwgItems.Nugget.Chromium).build(assets);
		// TODO: ItemGenerator.basic(SwgItems.Nugget.Cortosis).build(assets);
		ItemGenerator.basic(SwgItems.Nugget.Desh).build(assets);
		ItemGenerator.basic(SwgItems.Nugget.Diatium).build(assets);
		ItemGenerator.basic(SwgItems.Nugget.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Nugget.Lommite).build(assets);
		// TODO: ItemGenerator.basic(SwgItems.Nugget.Plasteel).build(assets);
		ItemGenerator.basic(SwgItems.Nugget.Titanium).build(assets);
		// TODO: ItemGenerator.basic(SwgItems.Nugget.Transparisteel).build(assets);
		ItemGenerator.basic(SwgItems.Nugget.Zersium).build(assets);

		ItemGenerator.basic(SwgItems.Pickaxe.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Pickaxe.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Pickaxe.Beskar).build(assets);

		ItemGenerator.basic(SwgItems.Shovel.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Shovel.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Shovel.Beskar).build(assets);
	}

	private static void generateBlocks(List<BuiltAsset> assets)
	{
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Barrel.MosEisley).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.OctagonOrange).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.OctagonGray).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.OctagonBlack).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.MosEisley).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.ImperialCube).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Door.TatooineHomeController).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Light.FloorWedge).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Light.WallCluster).build(assets);

		BlockGenerator.leaves(SwgBlocks.Leaves.Sequoia).build(assets);

		BlockGenerator.column(SwgBlocks.Log.Sequoia, Resources.identifier("block/sequoia_log_top"), Resources.identifier("block/sequoia_log_side")).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Machine.Spoked).build(assets);

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
		BlockGenerator.basic(SwgBlocks.MaterialBlock.Ionite).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.MoistureVaporator.Gx8).build(assets);

		BlockGenerator.basic(SwgBlocks.Ore.Beskar).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Chromium).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Cortosis).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Desh).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Diatium).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Ionite).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Lommite).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Titanium).build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Zersium).build(assets);

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

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Pipe.Thick).build(assets);

		BlockGenerator.cross(SwgBlocks.Plant.FunnelFlower).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.BlossomingFunnelFlower).build(assets);

		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.Tatooine).build(assets);
		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.TatooineCanyon).build(assets);

		BlockGenerator.basicRandomRotation(SwgBlocks.Dirt.DenseTatooine).build(assets);

		BlockGenerator.basic(SwgBlocks.Stone.MassassiBricks).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.MassassiSmooth).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.MassassiBrickStairs, Resources.identifier("block/massassi_stone_bricks")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.MassassiSmoothStairs, Resources.identifier("block/smooth_massassi_stone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.MassassiBrickSlab, Resources.identifier("block/massassi_stone_bricks")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.MassassiSmoothSlab, Resources.identifier("block/smooth_massassi_stone")).build(assets);

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

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Tank.Fusion).build(assets);

		BlockGenerator.staticColumn(SwgBlocks.Vent.Tatooine, Resources.identifier("block/tatooine_vent_top"), Resources.identifier("block/tatooine_vent_side")).build(assets);

		BlockGenerator.basic(SwgBlocks.Workbench.Blaster).build(assets);
		BlockGenerator.basic(SwgBlocks.Workbench.Lightsaber).build(assets);
	}
}
