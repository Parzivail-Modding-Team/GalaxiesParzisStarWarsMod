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
				5.0F,
				SwgBlocks.Ore.Beskar,
				SwgItems.Ingot.Beskar,
				SwgBlocks.MaterialBlock.Beskar
		);

		RecipeGenerator.buildMetal(
				assets,
				1.0F,
				SwgBlocks.Ore.Chromium,
				SwgItems.Ingot.Chromium,
				SwgItems.Nugget.Chromium,
				SwgBlocks.MaterialBlock.Chromium
		);

		RecipeGenerator.buildMetal(
				assets,
				4.0F,
				SwgBlocks.Ore.Cortosis,
				SwgItems.Ingot.Cortosis,
				SwgBlocks.MaterialBlock.Cortosis
		);

		RecipeGenerator.buildMetal(
				assets,
				0.4F,
				SwgBlocks.Ore.Desh,
				SwgItems.Ingot.Desh,
				SwgItems.Nugget.Desh,
				SwgBlocks.MaterialBlock.Desh
		);

		RecipeGenerator.buildMetal(
				assets,
				0.7F,
				SwgBlocks.Ore.Diatium,
				SwgItems.Ingot.Diatium,
				SwgItems.Nugget.Diatium,
				SwgBlocks.MaterialBlock.Diatium
		);

		// TODO: durasteel

		RecipeGenerator.buildMetal(
				assets,
				0.7F,
				SwgBlocks.Ore.Lommite,
				SwgItems.Ingot.Lommite,
				// TODO: nugget
				SwgBlocks.MaterialBlock.Lommite
		);

		// TODO: plasteel

		RecipeGenerator.buildMetal(
				assets,
				2.0F,
				SwgBlocks.Ore.Titanium,
				SwgItems.Ingot.Titanium,
				SwgItems.Nugget.Titanium,
				SwgBlocks.MaterialBlock.Titanium
		);

		RecipeGenerator.buildMetal(
				assets,
				0.7F,
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
				.grid3x3("ionite", SwgItems.Dust.Ionite, SwgBlocks.Sand.Desert, SwgItems.Dust.Ionite,
				                             SwgBlocks.Sand.Desert,SwgItems.Dust.Ionite,SwgBlocks.Sand.Desert,
				                            SwgItems.Dust.Ionite,SwgBlocks.Sand.Desert,SwgItems.Dust.Ionite)
				.build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.MaterialBlock.Ionite))
				.fill3x3("dust",SwgItems.Dust.Ionite)
				.build(assets);

		RecipeGenerator.Shapeless.of(new ItemStack(SwgItems.Dust.Ionite, 9), "block")
		                         .ingredient(SwgBlocks.MaterialBlock.Ionite)
		                         .build(assets);


		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.ZersiumRod, 3))
								.grid3x3("zersium", null, SwgItems.Ingot.Zersium, null,
		                                                       null, SwgItems.Ingot.Zersium, null,
		                                                      null, SwgItems.Ingot.Zersium, null)
								.build(assets);

		//Axes
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Axe.Durasteel))
		                      .grid3x3("crafting_left", SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel, null,
		                                                            SwgItems.Ingot.Durasteel,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                        null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .grid3x3("crafting_right", null, SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,SwgItems.Ingot.Durasteel,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Axe.Titanium))
		                      .grid3x3("crafting_left", SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, null,
		                                                          SwgItems.Ingot.Titanium,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .grid3x3("crafting_right", null, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,SwgItems.Ingot.Titanium,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Axe.Beskar))
		                      .grid3x3("crafting_left", SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar, null,
		                                                           SwgItems.Ingot.Beskar,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .grid3x3("crafting_right", null, SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,SwgItems.Ingot.Beskar,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);
		//Hoes
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Hoe.Durasteel))
		                      .grid3x3("crafting_left", SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel, null,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .grid3x3("crafting_right", null, SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel,
		                                                       null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Hoe.Titanium))
		                      .grid3x3("crafting_left", SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, null,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .grid3x3("crafting_right", null, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium,
		                                                           null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Hoe.Beskar))
		                      .grid3x3("crafting_left", SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar, null,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .grid3x3("crafting_right", null, SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar,
		                                                        null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                        null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);
		//Pickaxe
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Pickaxe.Durasteel))
		                      .grid3x3("crafting", SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                            null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Pickaxe.Titanium))
		                      .grid3x3("crafting", SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Pickaxe.Beskar))
		                      .grid3x3("crafting", SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar,
		                                                          null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                           null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);
		//Shovel
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Shovel.Durasteel))
		                      .grid3x3("crafting", null, SwgItems.Ingot.Durasteel, null,
		                                                       null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                        null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Shovel.Titanium))
		                      .grid3x3("crafting", null, SwgItems.Ingot.Titanium, null,
		                                                      null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                     null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Shovel.Beskar))
		                      .grid3x3("crafting", null, SwgItems.Ingot.Beskar, null,
		                                                       null,SwgItems.CraftingComponents.ZersiumRod,null,
		                                                         null,SwgItems.CraftingComponents.ZersiumRod,null)
		                      .build (assets);

		//Plasteel alternative recipes
		RecipeGenerator.Shaped.of(new ItemStack(Items.ACTIVATOR_RAIL, 6))
		                      .grid3x3("plasteel",
		                               SwgItems.Ingot.Plasteel, Items.STICK, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, Items.REDSTONE_TORCH, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, Items.STICK, SwgItems.Ingot.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.ANVIL))
				.grid3x3("plasteel",
				         SwgBlocks.MaterialBlock.Plasteel, SwgBlocks.MaterialBlock.Plasteel, SwgBlocks.MaterialBlock.Plasteel,
				         null, SwgItems.Ingot.Plasteel, null,
				         SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
				.build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.BLAST_FURNACE))
		                      .grid3x3("plasteel",
		                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, Items.FURNACE, SwgItems.Ingot.Plasteel,
		                               Items.SMOOTH_STONE, Items.SMOOTH_STONE, Items.SMOOTH_STONE)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.BUCKET))
		                      .grid3x2("plasteel",
		                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
		                               null, SwgItems.Ingot.Plasteel, null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.CAULDRON))
		                      .grid3x3("plasteel",
		                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel,SwgItems.Ingot.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.CHAIN))
		                      .grid1x3("plasteel",
		                               SwgItems.Nugget.Plasteel,
		                               SwgItems.Ingot.Plasteel,
		                               SwgItems.Nugget.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.COMPASS))
		                      .grid3x3("plasteel",
		                               null, SwgItems.Ingot.Plasteel, null,
		                               SwgItems.Ingot.Plasteel, Items.REDSTONE, SwgItems.Ingot.Plasteel,
		                               null, SwgItems.Ingot.Plasteel, null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.CROSSBOW))
		                      .grid3x3("plasteel",
		                               Items.STICK, SwgItems.Ingot.Plasteel, Items.STICK,
		                               Items.STRING, Items.TRIPWIRE_HOOK, Items.STRING,
		                               null, Items.STICK, null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.DETECTOR_RAIL, 6))
		                      .grid3x3("plasteel",
		                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, Items.STONE_PRESSURE_PLATE, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, Items.REDSTONE, SwgItems.Ingot.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shapeless.of(new ItemStack(Items.FLINT_AND_STEEL), "plasteel")
		                         .ingredient(SwgItems.Ingot.Plasteel)
		                         .ingredient(Items.FLINT)
		                         .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.HEAVY_WEIGHTED_PRESSURE_PLATE))
		                      .grid2x1("plasteel", SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.HOPPER))
		                      .grid3x3("plasteel",
		                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, Items.CHEST, SwgItems.Ingot.Plasteel,
		                               null, SwgItems.Ingot.Plasteel, null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.MINECART))
		                      .grid3x2("plasteel",
		                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.PISTON))
		                      .grid3x3("plasteel",
		                               Items.OAK_PLANKS, Items.OAK_PLANKS, Items.OAK_PLANKS,
		                               Items.COBBLESTONE, SwgItems.Ingot.Plasteel, Items.COBBLESTONE,
		                               Items.COBBLESTONE, Items.REDSTONE,Items.COBBLESTONE)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.RAIL,16))
		                      .grid3x3("plasteel",
		                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, Items.STICK, SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel, null,SwgItems.Ingot.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.SHEARS))
		                      .grid2x2("plasteel_left",
		                               SwgItems.Ingot.Plasteel, null,
		                               null, SwgItems.Ingot.Plasteel)
		                      .grid2x2("plasteel_right",
		                               null,SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel,null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.SHIELD))
		                      .grid3x3("plasteel",
		                               Items.OAK_PLANKS, SwgItems.Ingot.Plasteel, Items.OAK_PLANKS,
		                               Items.OAK_PLANKS, Items.OAK_PLANKS, Items.OAK_PLANKS,
		                              null, Items.OAK_PLANKS, null)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.SMITHING_TABLE))
		                      .grid2x3("plasteel",
		                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel,
		                               Items.OAK_PLANKS,Items.OAK_PLANKS,
		                               Items.OAK_PLANKS,Items.OAK_PLANKS)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.STONECUTTER))
		                      .grid3x2("plasteel",
		                               null, SwgItems.Ingot.Plasteel, null,
		                               Items.STONE,Items.STONE,Items.STONE)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.TRIPWIRE_HOOK, 2))
		                      .grid1x3("plasteel",
		                               SwgItems.Ingot.Plasteel,
		                               Items.STICK,
		                               Items.OAK_PLANKS)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.LANTERN))
		                      .grid3x3("plasteel",
		                               SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel,
		                               SwgItems.Nugget.Plasteel, Items.TORCH, SwgItems.Nugget.Plasteel,
		                               SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel,SwgItems.Nugget.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.SOUL_LANTERN))
		                      .grid3x3("plasteel",
		                               SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel,
		                               SwgItems.Nugget.Plasteel, Items.SOUL_TORCH, SwgItems.Nugget.Plasteel,
		                               SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel,SwgItems.Nugget.Plasteel)
		                      .build (assets);

		//Pourstone
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.PourstoneStairs, 6))
		                      .grid3x3("pourstone_left",
		                               SwgBlocks.Stone.Pourstone, null, null,
		                               SwgBlocks.Stone.Pourstone,SwgBlocks.Stone.Pourstone,null,
		                               SwgBlocks.Stone.Pourstone,SwgBlocks.Stone.Pourstone,SwgBlocks.Stone.Pourstone)
		                      .grid3x3("pourstone_right",
		                               null, null, SwgBlocks.Stone.Pourstone,
		                               null,SwgBlocks.Stone.Pourstone,SwgBlocks.Stone.Pourstone,
		                               SwgBlocks.Stone.Pourstone,SwgBlocks.Stone.Pourstone,SwgBlocks.Stone.Pourstone)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.PourstoneSlab, 6))
		                      .grid3x1("pourstone",
		                               SwgBlocks.Stone.Pourstone,SwgBlocks.Stone.Pourstone,SwgBlocks.Stone.Pourstone)
		                      .build (assets);
		RecipeGenerator.Cooking.smelting("smelting", SwgBlocks.Dirt.WetPourstone, SwgBlocks.Stone.Pourstone).experience(0.1F)
		                       .build (assets);
		RecipeGenerator.Cooking.smelting("smelting", SwgBlocks.Dirt.WetPourstoneStairs, SwgBlocks.Stone.PourstoneStairs).experience(0.1F)
		                       .build (assets);
		RecipeGenerator.Cooking.smelting("smelting", SwgBlocks.Dirt.WetPourstoneSlab, SwgBlocks.Stone.PourstoneSlab).experience(0.1F)
		                       .build (assets);

		//Light Pourstone
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.LightPourstoneStairs, 6))
		                      .grid3x3("light_pourstone_left",
		                               SwgBlocks.Stone.LightPourstone, null, null,
		                               SwgBlocks.Stone.LightPourstone,SwgBlocks.Stone.LightPourstone,null,
		                               SwgBlocks.Stone.LightPourstone,SwgBlocks.Stone.LightPourstone,SwgBlocks.Stone.LightPourstone)
		                      .grid3x3("light_pourstone_right",
		                               null, null, SwgBlocks.Stone.LightPourstone,
		                               null,SwgBlocks.Stone.LightPourstone,SwgBlocks.Stone.LightPourstone,
		                               SwgBlocks.Stone.LightPourstone,SwgBlocks.Stone.LightPourstone,SwgBlocks.Stone.LightPourstone)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.LightPourstoneSlab, 6))
		                      .grid3x1("light_pourstone",
		                               SwgBlocks.Stone.LightPourstone,SwgBlocks.Stone.LightPourstone,SwgBlocks.Stone.LightPourstone)
		                      .build (assets);
		//Ilum Stone
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumStairs, 6))
		                      .grid3x3("ilum_stone_left",
		                               SwgBlocks.Stone.Ilum, null, null,
		                               SwgBlocks.Stone.Ilum,SwgBlocks.Stone.Ilum,null,
		                               SwgBlocks.Stone.Ilum,SwgBlocks.Stone.Ilum,SwgBlocks.Stone.Ilum)
		                      .grid3x3("ilum_stone_right",
		                               null, null, SwgBlocks.Stone.Ilum,
		                               null,SwgBlocks.Stone.Ilum,SwgBlocks.Stone.Ilum,
		                               SwgBlocks.Stone.Ilum,SwgBlocks.Stone.Ilum,SwgBlocks.Stone.Ilum)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumSlab, 6))
		                      .grid3x1("ilum_stone",
		                               SwgBlocks.Stone.Ilum,SwgBlocks.Stone.Ilum,SwgBlocks.Stone.Ilum)
		                      .build (assets);
		RecipeGenerator.Cooking.smelting("smooth_ilum_stone",SwgBlocks.Stone.Ilum,SwgBlocks.Stone.IlumSmooth).experience(0.1F)
		                       .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumSmoothSlab, 6))
		                      .grid3x1("smooth_ilum_stone",
		                               SwgBlocks.Stone.IlumSmooth,SwgBlocks.Stone.IlumSmooth,SwgBlocks.Stone.IlumSmooth)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumBricks, 4))
		                      .fill2x2("ilum_stone",SwgBlocks.Stone.Ilum)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumBrickStairs, 6))
		                      .grid3x3("ilum_stone_left",
		                               SwgBlocks.Stone.IlumBricks, null, null,
		                               SwgBlocks.Stone.IlumBricks,SwgBlocks.Stone.IlumBricks,null,
		                               SwgBlocks.Stone.IlumBricks,SwgBlocks.Stone.IlumBricks,SwgBlocks.Stone.IlumBricks)
		                      .grid3x3("ilum_stone_right",
		                               null, null, SwgBlocks.Stone.IlumBricks,
		                               null,SwgBlocks.Stone.IlumBricks,SwgBlocks.Stone.IlumBricks,
		                               SwgBlocks.Stone.IlumBricks,SwgBlocks.Stone.IlumBricks,SwgBlocks.Stone.IlumBricks)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumBrickSlab, 6))
		                      .grid3x1("ilum_stone",
		                               SwgBlocks.Stone.IlumBricks,SwgBlocks.Stone.IlumBricks,SwgBlocks.Stone.IlumBricks)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumChiseledBricks))
		                      .fill1x2("ilum_stone", SwgBlocks.Stone.IlumBrickSlab)
		                      .build (assets);
		//Wet Pourstone
		RecipeGenerator.Shapeless.of(new ItemStack(SwgBlocks.Dirt.WetPourstone, 8),"crafting")
		                         .ingredient(SwgBlocks.Dirt.DesertLoam).ingredient(SwgBlocks.Dirt.DesertLoam).ingredient(SwgBlocks.Dirt.DesertLoam)
		                         .ingredient(SwgBlocks.Sand.Desert).ingredient(SwgBlocks.Sand.Desert).ingredient(SwgBlocks.Sand.Desert)
		                         .ingredient(Items.WHEAT).ingredient(Items.WHEAT)
		                         .ingredient(Items.WATER_BUCKET)
		                         .build (assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Dirt.WetPourstoneStairs, 6))
		                      .grid3x3("wet_pourstone_left",
		                               SwgBlocks.Dirt.WetPourstone, null, null,
		                               SwgBlocks.Dirt.WetPourstone,SwgBlocks.Dirt.WetPourstone,null,
		                               SwgBlocks.Dirt.WetPourstone,SwgBlocks.Dirt.WetPourstone,SwgBlocks.Dirt.WetPourstone)
		                      .grid3x3("wet_pourstone_right",
		                               null, null, SwgBlocks.Dirt.WetPourstone,
		                               null,SwgBlocks.Dirt.WetPourstone,SwgBlocks.Dirt.WetPourstone,
		                               SwgBlocks.Dirt.WetPourstone,SwgBlocks.Dirt.WetPourstone,SwgBlocks.Dirt.WetPourstone)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Dirt.WetPourstoneSlab, 6))
		                      .grid3x1("wet_pourstone",
		                               SwgBlocks.Dirt.WetPourstone,SwgBlocks.Dirt.WetPourstone,SwgBlocks.Dirt.WetPourstone)
		                      .build (assets);
		//Light Blocks
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.LightBlock, 3))
		                      .grid3x3("crafting",
		                               Items.GLASS_PANE, Items.GLASS_PANE, Items.GLASS_PANE,
		                               Items.GLASS_PANE, SwgItems.CraftingComponents.LightPanel,Items.GLASS_PANE,
		                               Items.GLASS_PANE,Items.GLASS_PANE,Items.GLASS_PANE)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.FloorWedge, 4))
		                      .grid3x2("crafting",
		                               null, SwgItems.Ingot.Plasteel, null,
		                               SwgItems.Ingot.Plasteel, SwgItems.CraftingComponents.LightPanel,SwgItems.Ingot.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.WallCluster, 4))
		                      .grid3x3("crafting",
		                               null, SwgItems.Ingot.Plasteel, null,
		                               SwgItems.Ingot.Plasteel, SwgItems.CraftingComponents.LightPanel,SwgItems.Ingot.Plasteel,
		                               null, SwgItems.Ingot.Plasteel, null)
		                      .build (assets);

		//Decoration Blocks and Machines
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Vent.Air, 3))
		                      .grid3x3("crafting",
		                               SwgItems.Ingot.Plasteel,SwgItems.Nugget.Plasteel,SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel,SwgItems.Nugget.Plasteel,SwgItems.Ingot.Plasteel,
		                               SwgItems.Ingot.Plasteel,SwgItems.Nugget.Plasteel,SwgItems.Ingot.Plasteel)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.OrangeKyber))
		                      .grid3x3("crafting",
		                               SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium,
		                               SwgItems.Ingot.Titanium,Items.ORANGE_DYE,SwgItems.Ingot.Titanium,
		                               SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.GrayKyber))
		                      .grid3x3("crafting",
		                               SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium,
		                               SwgItems.Ingot.Titanium,Items.GRAY_DYE,SwgItems.Ingot.Titanium,
		                               SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.BlackKyber))
		                      .grid3x3("crafting",
		                               SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium,
		                               SwgItems.Ingot.Titanium,Items.BLACK_DYE,SwgItems.Ingot.Titanium,
		                               SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium,SwgItems.Ingot.Titanium)
		                      .build (assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.Toolbox))
		                      .grid3x3("crafting",
		                               SwgItems.Ingot.Desh,SwgItems.Ingot.Desh,SwgItems.Ingot.Desh,
		                               SwgItems.Ingot.Desh,null,SwgItems.Ingot.Desh,
		                               Items.IRON_INGOT,Items.IRON_INGOT,Items.IRON_INGOT)
		                      .build (assets);
	}

	private static void generateItems(List<BuiltAsset> assets)
	{
		ItemGenerator.basic(SwgItems.Axe.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Axe.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Axe.Beskar).build(assets);

		ItemGenerator.empty(SwgItems.Blaster.Blaster).build(assets);

		BlockGenerator.basicRandomRotation(SwgBlocks.Dirt.DesertLoam).build(assets);
		BlockGenerator.basic(SwgBlocks.Dirt.WetPourstone).build(assets);
		BlockGenerator.stairs(SwgBlocks.Dirt.WetPourstoneStairs, Resources.identifier("block/wet_pourstone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Dirt.WetPourstoneSlab, Resources.identifier("block/wet_pourstone")).build(assets);

		ItemGenerator.basic(SwgItems.CraftingComponents.ElectricMotor).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.LightPanel).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.DisplayPanel).build(assets);
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
		ItemGenerator.basic(SwgItems.Nugget.Plasteel).build(assets);
		ItemGenerator.basic(SwgItems.Nugget.Titanium).build(assets);
		// TODO: ItemGenerator.basic(SwgItems.Nugget.Transparisteel).build(assets);
		ItemGenerator.basic(SwgItems.Nugget.Zersium).build(assets);

		ItemGenerator.basic(SwgItems.Pickaxe.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Pickaxe.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Pickaxe.Beskar).build(assets);

		ItemGenerator.basic(SwgItems.Shovel.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Shovel.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Shovel.Beskar).build(assets);

		ItemGenerator.basic(SwgItems.Spawners.XwingT65b).build(assets);
	}

	private static void generateBlocks(List<BuiltAsset> assets)
	{
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Barrel.Desh).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.OrangeKyber).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.GrayKyber).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.BlackKyber).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.Toolbox).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.Imperial).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Door.TatooineHomeController).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Light.FloorWedge).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Light.WallCluster).build(assets);
		BlockGenerator.basic(SwgBlocks.Light.LightBlock).build(assets);

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

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Pipe.Large).build(assets);

		BlockGenerator.cross(SwgBlocks.Plant.FunnelFlower).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.BlossomingFunnelFlower).build(assets);

		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.Desert).build(assets);
		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.DesertCanyon).build(assets);

		BlockGenerator.basic(SwgBlocks.Stone.MassassiBricks).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.MassassiSmooth).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.MassassiBrickStairs, Resources.identifier("block/massassi_stone_bricks")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.MassassiSmoothStairs, Resources.identifier("block/smooth_massassi_stone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.MassassiBrickSlab, Resources.identifier("block/massassi_stone_bricks")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.MassassiSmoothSlab, Resources.identifier("block/smooth_massassi_stone")).build(assets);

		BlockGenerator.basic(SwgBlocks.Stone.Pourstone).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.PourstoneStairs, Resources.identifier("block/pourstone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.PourstoneSlab, Resources.identifier("block/pourstone")).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.LightPourstone).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.LightPourstoneStairs, Resources.identifier("block/light_pourstone")).build(assets);
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

		BlockGenerator.staticColumn(SwgBlocks.Vent.Air, Resources.identifier("block/air_vent_top"), Resources.identifier("block/air_vent_side")).build(assets);

		BlockGenerator.basic(SwgBlocks.Workbench.Blaster).build(assets);
		BlockGenerator.basic(SwgBlocks.Workbench.Lightsaber).build(assets);
	}
}
