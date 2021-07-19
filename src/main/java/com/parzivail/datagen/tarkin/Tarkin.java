package com.parzivail.datagen.tarkin;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.crop.HkakBushBlock;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.block.InvertedLampBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * T.A.R.K.I.N. - Text Asset Record-Keeping, Integration, and Normalization
 */
public class Tarkin
{
	public static void main(String arg) throws Exception
	{
		Lumberjack.setLogHeader("TARKIN");

		//		AssetGenerator.setOutputRoot(Paths.get("..", "TEMP", "src", "main", "resources"));

		List<BuiltAsset> assets = new ArrayList<>();

		generateBlocks(assets);
		generateItems(assets);
		generateRecipes(assets);

		generateLangEntries(assets);

		BuiltAsset.nukeRecipeDir();
		BuiltAsset.nukeBlockstateDir();
		BuiltAsset.nukeBlockModelJsons();
		BuiltAsset.nukeItemModelJsons();

		for (var asset : assets)
		{
			Lumberjack.log("Wrote %s", asset.getFilename());
			asset.write();
		}

		// Synchronize the keys of the en_us locale
		BuiltAsset.mergeLanguageKeys(Resources.id("en_us_temp"), Resources.id("en_us"));
	}

	private static void generateLangEntries(List<BuiltAsset> assets)
	{
		var lang = new LanguageBuilder(Resources.id(LanguageProvider.OUTPUT_LOCALE));

		// Commands

		var speciesCmd = lang.command("species");
		speciesCmd.dot("invalid").build(assets);
		speciesCmd.dot("variant").dot("invalid").build(assets);

		// Containers
		lang.container("blaster_workbench").build(assets);
		lang.container("imperial_cube_crate").build(assets);
		lang.container("kyber_crate").build(assets);
		lang.container("lightsaber_forge").build(assets);

		var gx8 = lang.container("moisture_vaporator_gx8");
		gx8.build(assets);
		gx8.dot("idle").build(assets);

		lang.container("mos_eisley_crate").build(assets);
		lang.container("segmented_crate").build(assets);
		lang.container("toolbox").build(assets);

		// Entities
		lang.entity(SwgEntities.Misc.BlasterBolt).build(assets);
		lang.entity(SwgEntities.Misc.KinematicTest).build(assets);
		lang.entity(SwgEntities.Misc.ThrownLightsaber).build(assets);
		lang.entity(SwgEntities.Ship.T65bXwing).build(assets);
		lang.entity(SwgEntities.Speeder.X34).build(assets);
		lang.entity(SwgEntities.Fish.Faa).build(assets);
		lang.entity(SwgEntities.Fish.Laa).build(assets);
		lang.entity(SwgEntities.Amphibian.Worrt).build(assets);

		// Screen
		lang.screen("apply").build(assets);

		var speciesSelect = lang.screen("species_select");
		speciesSelect.build(assets);
		speciesSelect.dot("use_female_model").build(assets);

		// Dynamic items
		lang.item("blaster_a280").build(assets);
		lang.item("blaster_cycler").build(assets);
		lang.item("blaster_dc15").build(assets);
		lang.item("blaster_dc15a").build(assets);
		lang.item("blaster_dh17").build(assets);
		lang.item("blaster_dl18").build(assets);
		lang.item("blaster_dl44").build(assets);
		lang.item("blaster_dlt19").build(assets);
		lang.item("blaster_e11").build(assets);
		lang.item("blaster_ee3").build(assets);
		lang.item("blaster_jawa_ion").build(assets);
		lang.item("blaster_rk3").build(assets);
		lang.item("blaster_rt97c").build(assets);
		lang.item("blaster_t21").build(assets);

		// Item groups
		lang.itemGroup("main").build(assets);

		// Key categories
		lang.keyCategory("pswg").build(assets);

		// Keys
		lang.key(Client.KEY_LIGHTSABER_TOGGLE).build(assets);
		lang.key(Client.KEY_SHIP_INPUT_MODE_OVERRIDE).build(assets);
		lang.key(Client.KEY_SPECIES_SELECT).build(assets);

		// Messages
		lang.message("update").build(assets);

		// Species
		var speciesLangBase = lang.cloneWithRoot("species").modid();

		speciesLangBase.dot("none").build(assets);

		for (var species : SwgSpeciesRegistry.getSpecies())
		{
			var speciesSlug = species.getSlug();

			var speciesLang = speciesLangBase.dot(speciesSlug.getPath());
			speciesLang.build(assets);

			for (var variable : species.getVariables())
			{
				var variableLang = speciesLang.dot(variable.getName());
				variableLang.build(assets);

				for (var value : variable.getPossibleValues())
					variableLang.dot(value).build(assets);
			}
		}

		// Autoconfig
		var autoconfig = lang.cloneWithRoot("text").dot("autoconfig").modid();
		autoconfig.dot("title").build(assets);

		var autoconfigOption = autoconfig.dot("option");

		var autoconfigOptionInput = autoconfigOption.dot("input");
		autoconfigOptionInput.build(assets);
		autoconfigOptionInput.dot("@Tooltip").build(assets);

		autoconfigOptionInput.dot("shipRollPriority").build(assets);
		autoconfigOptionInput.dot("shipRollPriority").dot("@Tooltip").build(assets);
	}

	private static void generateRecipes(List<BuiltAsset> assets)
	{
		//Materials
		{
			RecipeGenerator.Shapeless.of(new ItemStack(SwgItems.Dust.Ionite), null)
			                         .ingredient(SwgItems.Crystal.Ionite)
			                         .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.MaterialBlock.Ionite))
			                      .fill3x3(null, SwgItems.Dust.Ionite)
			                      .build(assets);
			RecipeGenerator.Shapeless.of(new ItemStack(SwgItems.Dust.Ionite, 9), null)
			                         .ingredient(SwgBlocks.MaterialBlock.Ionite)
			                         .build(assets);
			RecipeGenerator.Cooking.smelting("smelting", SwgBlocks.Ore.Ionite, SwgItems.Dust.Ionite).experience(0.7F)
			                       .build(assets);
			//Metals
			{
				RecipeGenerator.buildMetal(
						assets,
						5.0F,
						SwgBlocks.Ore.Beskar,
						SwgItems.RawOre.Beskar,
						SwgItems.Ingot.Beskar,
						SwgBlocks.MaterialBlock.Beskar
				);

				RecipeGenerator.buildMetal(
						assets,
						1.0F,
						SwgBlocks.Ore.Chromium,
						SwgItems.RawOre.Chromium,
						SwgItems.Ingot.Chromium,
						SwgItems.Nugget.Chromium,
						SwgBlocks.MaterialBlock.Chromium
				);

				RecipeGenerator.buildMetal(
						assets,
						4.0F,
						SwgBlocks.Ore.Cortosis,
						SwgItems.RawOre.Cortosis,
						SwgItems.Ingot.Cortosis,
						SwgBlocks.MaterialBlock.Cortosis
				);

				RecipeGenerator.buildMetal(
						assets,
						0.4F,
						SwgBlocks.Ore.Desh,
						SwgItems.RawOre.Desh,
						SwgItems.Ingot.Desh,
						SwgItems.Nugget.Desh,
						SwgBlocks.MaterialBlock.Desh
				);

				RecipeGenerator.buildMetal(
						assets,
						0.7F,
						SwgBlocks.Ore.Diatium,
						SwgItems.RawOre.Diatium,
						SwgItems.Ingot.Diatium,
						SwgItems.Nugget.Diatium,
						SwgBlocks.MaterialBlock.Diatium
				);

				// TODO: durasteel

				RecipeGenerator.buildMetal(
						assets,
						0.7F,
						SwgBlocks.Ore.Lommite,
						// TODO: why is this in Crystal if it is a metal?
						SwgItems.Crystal.Lommite,
						SwgItems.Ingot.Lommite,
						// TODO: nugget
						SwgBlocks.MaterialBlock.Lommite
				);

				// TODO: plasteel

				RecipeGenerator.buildMetal(
						assets,
						2.0F,
						SwgBlocks.Ore.Titanium,
						SwgItems.RawOre.Titanium,
						SwgItems.Ingot.Titanium,
						SwgItems.Nugget.Titanium,
						SwgBlocks.MaterialBlock.Titanium
				);

				RecipeGenerator.buildMetal(
						assets,
						0.7F,
						SwgBlocks.Ore.Zersium,
						SwgItems.RawOre.Zersium,
						SwgItems.Ingot.Zersium,
						SwgItems.Nugget.Zersium,
						SwgBlocks.MaterialBlock.Zersium
				);
			}
		}

		//Food
		{
			RecipeGenerator.buildFood(assets, SwgItems.Food.BanthaChop, SwgItems.Food.BanthaSteak);
			RecipeGenerator.buildFood(assets, SwgItems.Food.GizkaChop, SwgItems.Food.GizkaSteak);
			RecipeGenerator.buildFood(assets, SwgItems.Food.MynockWing, SwgItems.Food.FriedMynockWing);
			RecipeGenerator.buildFood(assets, SwgItems.Food.NerfChop, SwgItems.Food.NerfSteak);
			RecipeGenerator.buildFood(assets, SwgItems.Food.QrikkiBread, SwgItems.Food.QrikkiWaffle);
		}

		//Vanilla Blocks and Items
		{
			RecipeGenerator.Shapeless.of(new ItemStack(Items.BLUE_DYE), "ionite")
			                         .ingredient(SwgItems.Dust.Ionite)
			                         .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(Items.TNT))
			                      .grid3x3("ionite",
			                               SwgItems.Dust.Ionite, ItemTags.SAND, SwgItems.Dust.Ionite,
			                               ItemTags.SAND, SwgItems.Dust.Ionite, ItemTags.SAND,
			                               SwgItems.Dust.Ionite, ItemTags.SAND, SwgItems.Dust.Ionite)
			                      .build(assets);

			//Plasteel alternative recipes
			{
				RecipeGenerator.Shaped.of(new ItemStack(Items.ANVIL))
				                      .grid3x3("plasteel",
				                               SwgBlocks.MaterialBlock.Plasteel, SwgBlocks.MaterialBlock.Plasteel, SwgBlocks.MaterialBlock.Plasteel,
				                               null, SwgItems.Ingot.Plasteel, null,
				                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.BLAST_FURNACE))
				                      .grid3x3("plasteel",
				                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, Items.FURNACE, SwgItems.Ingot.Plasteel,
				                               Items.SMOOTH_STONE, Items.SMOOTH_STONE, Items.SMOOTH_STONE)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.BUCKET))
				                      .grid3x2("plasteel",
				                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
				                               null, SwgItems.Ingot.Plasteel, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.CAULDRON))
				                      .grid3x3("plasteel",
				                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.CHAIN))
				                      .grid1x3("plasteel",
				                               SwgItems.Nugget.Plasteel,
				                               SwgItems.Ingot.Plasteel,
				                               SwgItems.Nugget.Plasteel)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.COMPASS))
				                      .grid3x3("plasteel",
				                               null, SwgItems.Ingot.Plasteel, null,
				                               SwgItems.Ingot.Plasteel, Items.REDSTONE, SwgItems.Ingot.Plasteel,
				                               null, SwgItems.Ingot.Plasteel, null)
				                      .build(assets);
				RecipeGenerator.Shaped.of(new ItemStack(Items.SHEARS))
				                      .grid2x2("plasteel_left",
				                               SwgItems.Ingot.Plasteel, null,
				                               null, SwgItems.Ingot.Plasteel)
				                      .grid2x2("plasteel_right",
				                               null, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, null)
				                      .build(assets);
				RecipeGenerator.Shapeless.of(new ItemStack(Items.FLINT_AND_STEEL), "plasteel")
				                         .ingredient(SwgItems.Ingot.Plasteel)
				                         .ingredient(Items.FLINT)
				                         .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.CROSSBOW))
				                      .grid3x3("plasteel",
				                               Items.STICK, SwgItems.Ingot.Plasteel, Items.STICK,
				                               Items.STRING, Items.TRIPWIRE_HOOK, Items.STRING,
				                               null, Items.STICK, null)
				                      .build(assets);
				RecipeGenerator.Shaped.of(new ItemStack(Items.SHIELD))
				                      .grid3x3("plasteel",
				                               ItemTags.PLANKS, SwgItems.Ingot.Plasteel, ItemTags.PLANKS,
				                               ItemTags.PLANKS, ItemTags.PLANKS, ItemTags.PLANKS,
				                               null, ItemTags.PLANKS, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.HEAVY_WEIGHTED_PRESSURE_PLATE))
				                      .grid2x1("plasteel", SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.HOPPER))
				                      .grid3x3("plasteel",
				                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, Items.CHEST, SwgItems.Ingot.Plasteel,
				                               null, SwgItems.Ingot.Plasteel, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.MINECART))
				                      .grid3x2("plasteel",
				                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
				                      .build(assets);
				RecipeGenerator.Shaped.of(new ItemStack(Items.RAIL, 16))
				                      .grid3x3("plasteel",
				                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, Items.STICK, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel)
				                      .build(assets);
				RecipeGenerator.Shaped.of(new ItemStack(Items.ACTIVATOR_RAIL, 6))
				                      .grid3x3("plasteel",
				                               SwgItems.Ingot.Plasteel, Items.STICK, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, Items.REDSTONE_TORCH, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, Items.STICK, SwgItems.Ingot.Plasteel)
				                      .build(assets);
				RecipeGenerator.Shaped.of(new ItemStack(Items.DETECTOR_RAIL, 6))
				                      .grid3x3("plasteel",
				                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, Items.STONE_PRESSURE_PLATE, SwgItems.Ingot.Plasteel,
				                               SwgItems.Ingot.Plasteel, Items.REDSTONE, SwgItems.Ingot.Plasteel)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.PISTON))
				                      .grid3x3("plasteel",
				                               ItemTags.PLANKS, ItemTags.PLANKS, ItemTags.PLANKS,
				                               Items.COBBLESTONE, SwgItems.Ingot.Plasteel, Items.COBBLESTONE,
				                               Items.COBBLESTONE, Items.REDSTONE, Items.COBBLESTONE)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.SMITHING_TABLE))
				                      .grid2x3("plasteel",
				                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel,
				                               ItemTags.PLANKS, ItemTags.PLANKS,
				                               ItemTags.PLANKS, ItemTags.PLANKS)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.STONECUTTER))
				                      .grid3x2("plasteel",
				                               null, SwgItems.Ingot.Plasteel, null,
				                               Items.STONE, Items.STONE, Items.STONE)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.TRIPWIRE_HOOK, 2))
				                      .grid1x3("plasteel",
				                               SwgItems.Ingot.Plasteel,
				                               Items.STICK,
				                               ItemTags.PLANKS)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(Items.LANTERN))
				                      .grid3x3("plasteel",
				                               SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel,
				                               SwgItems.Nugget.Plasteel, Items.TORCH, SwgItems.Nugget.Plasteel,
				                               SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel)
				                      .build(assets);
				RecipeGenerator.Shaped.of(new ItemStack(Items.SOUL_LANTERN))
				                      .grid3x3("plasteel",
				                               SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel,
				                               SwgItems.Nugget.Plasteel, Items.SOUL_TORCH, SwgItems.Nugget.Plasteel,
				                               SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel, SwgItems.Nugget.Plasteel)
				                      .build(assets);
			}
		}

		//Tools
		{
			//Axes
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Axe.Durasteel))
				                      .grid3x3("crafting_left",
				                               SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel, null,
				                               SwgItems.Ingot.Durasteel, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .grid3x3("crafting_right",
				                               null, SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel,
				                               null, SwgItems.CraftingComponents.ZersiumRod, SwgItems.Ingot.Durasteel,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Axe.Titanium))
				                      .grid3x3("crafting_left",
				                               SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, null,
				                               SwgItems.Ingot.Titanium, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .grid3x3("crafting_right",
				                               null, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium,
				                               null, SwgItems.CraftingComponents.ZersiumRod, SwgItems.Ingot.Titanium,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Axe.Beskar))
				                      .grid3x3("crafting_left",
				                               SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar, null,
				                               SwgItems.Ingot.Beskar, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .grid3x3("crafting_right",
				                               null, SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar,
				                               null, SwgItems.CraftingComponents.ZersiumRod, SwgItems.Ingot.Beskar,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);
			}
			//Hoes
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Hoe.Durasteel))
				                      .grid3x3("crafting_left",
				                               SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .grid3x3("crafting_right",
				                               null, SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Hoe.Titanium))
				                      .grid3x3("crafting_left",
				                               SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .grid3x3("crafting_right",
				                               null, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Hoe.Beskar))
				                      .grid3x3("crafting_left",
				                               SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .grid3x3("crafting_right",
				                               null, SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);
			}
			//Pickaxes
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Pickaxe.Durasteel))
				                      .grid3x3(null,
				                               SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Pickaxe.Titanium))
				                      .grid3x3(null,
				                               SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Pickaxe.Beskar))
				                      .grid3x3(null,
				                               SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar, SwgItems.Ingot.Beskar,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);
			}
			//Shovels
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Shovel.Durasteel))
				                      .grid3x3(null,
				                               null, SwgItems.Ingot.Durasteel, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Shovel.Titanium))
				                      .grid3x3(null,
				                               null, SwgItems.Ingot.Titanium, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Shovel.Beskar))
				                      .grid3x3(null,
				                               null, SwgItems.Ingot.Beskar, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null,
				                               null, SwgItems.CraftingComponents.ZersiumRod, null)
				                      .build(assets);
			}
		}

		//Pourstone Blocks
		{
			//Wet Pourstone
			{
				RecipeGenerator.Shapeless.of(new ItemStack(SwgBlocks.Dirt.WetPourstone, 8), null)
				                         .ingredient(SwgBlocks.Dirt.DesertLoam).ingredient(SwgBlocks.Dirt.DesertLoam).ingredient(SwgBlocks.Dirt.DesertLoam)
				                         .ingredient(ItemTags.SAND).ingredient(ItemTags.SAND).ingredient(ItemTags.SAND)
				                         .ingredient(Items.WHEAT).ingredient(Items.WHEAT)
				                         .ingredient(Items.WATER_BUCKET)
				                         .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Dirt.WetPourstoneStairs, 6))
				                      .grid3x3("crafting_left",
				                               SwgBlocks.Dirt.WetPourstone, null, null,
				                               SwgBlocks.Dirt.WetPourstone, SwgBlocks.Dirt.WetPourstone, null,
				                               SwgBlocks.Dirt.WetPourstone, SwgBlocks.Dirt.WetPourstone, SwgBlocks.Dirt.WetPourstone)
				                      .grid3x3("crafting_right",
				                               null, null, SwgBlocks.Dirt.WetPourstone,
				                               null, SwgBlocks.Dirt.WetPourstone, SwgBlocks.Dirt.WetPourstone,
				                               SwgBlocks.Dirt.WetPourstone, SwgBlocks.Dirt.WetPourstone, SwgBlocks.Dirt.WetPourstone)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Dirt.WetPourstoneSlab, 6))
				                      .grid3x1(null,
				                               SwgBlocks.Dirt.WetPourstone, SwgBlocks.Dirt.WetPourstone, SwgBlocks.Dirt.WetPourstone)
				                      .build(assets);
			}

			//Ruined Wet Pourstone
			{
				RecipeGenerator.Shapeless.of(new ItemStack(SwgBlocks.Dirt.RuinedWetPourstone, 8), null)
				                         .ingredient(SwgBlocks.Dirt.DesertLoam).ingredient(SwgBlocks.Dirt.DesertLoam).ingredient(SwgBlocks.Dirt.DesertLoam)
				                         .ingredient(ItemTags.SAND).ingredient(ItemTags.SAND).ingredient(ItemTags.SAND)
				                         .ingredient(Items.WHEAT).ingredient(Items.WHEAT)
				                         .ingredient(Items.WATER_BUCKET)
				                         .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Dirt.RuinedWetPourstoneStairs, 6))
				                      .grid3x3("crafting_left",
				                               SwgBlocks.Dirt.RuinedWetPourstone, null, null,
				                               SwgBlocks.Dirt.RuinedWetPourstone, SwgBlocks.Dirt.RuinedWetPourstone, null,
				                               SwgBlocks.Dirt.RuinedWetPourstone, SwgBlocks.Dirt.RuinedWetPourstone, SwgBlocks.Dirt.RuinedWetPourstone)
				                      .grid3x3("crafting_right",
				                               null, null, SwgBlocks.Dirt.RuinedWetPourstone,
				                               null, SwgBlocks.Dirt.RuinedWetPourstone, SwgBlocks.Dirt.RuinedWetPourstone,
				                               SwgBlocks.Dirt.RuinedWetPourstone, SwgBlocks.Dirt.RuinedWetPourstone, SwgBlocks.Dirt.RuinedWetPourstone)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Dirt.RuinedWetPourstoneSlab, 6))
				                      .grid3x1(null,
				                               SwgBlocks.Dirt.RuinedWetPourstone, SwgBlocks.Dirt.RuinedWetPourstone, SwgBlocks.Dirt.RuinedWetPourstone)
				                      .build(assets);
			}

			//Pourstone
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.PourstoneStairs, 6))
				                      .grid3x3("crafting_left",
				                               SwgBlocks.Stone.Pourstone, null, null,
				                               SwgBlocks.Stone.Pourstone, SwgBlocks.Stone.Pourstone, null,
				                               SwgBlocks.Stone.Pourstone, SwgBlocks.Stone.Pourstone, SwgBlocks.Stone.Pourstone)
				                      .grid3x3("crafting_right",
				                               null, null, SwgBlocks.Stone.Pourstone,
				                               null, SwgBlocks.Stone.Pourstone, SwgBlocks.Stone.Pourstone,
				                               SwgBlocks.Stone.Pourstone, SwgBlocks.Stone.Pourstone, SwgBlocks.Stone.Pourstone)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.PourstoneSlab, 6))
				                      .grid3x1("crafting",
				                               SwgBlocks.Stone.Pourstone, SwgBlocks.Stone.Pourstone, SwgBlocks.Stone.Pourstone)
				                      .build(assets);

				RecipeGenerator.Cooking.smelting("smelting", SwgBlocks.Dirt.WetPourstone, SwgBlocks.Stone.Pourstone).experience(0.1F)
				                       .build(assets);
				RecipeGenerator.Cooking.smelting("smelting", SwgBlocks.Dirt.WetPourstoneStairs, SwgBlocks.Stone.PourstoneStairs).experience(0.1F)
				                       .build(assets);
				RecipeGenerator.Cooking.smelting("smelting", SwgBlocks.Dirt.WetPourstoneSlab, SwgBlocks.Stone.PourstoneSlab).experience(0.1F)
				                       .build(assets);
			}

			//Light Pourstone
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.LightPourstoneStairs, 6))
				                      .grid3x3("crafting_left",
				                               SwgBlocks.Stone.LightPourstone, null, null,
				                               SwgBlocks.Stone.LightPourstone, SwgBlocks.Stone.LightPourstone, null,
				                               SwgBlocks.Stone.LightPourstone, SwgBlocks.Stone.LightPourstone, SwgBlocks.Stone.LightPourstone)
				                      .grid3x3("crafting_right",
				                               null, null, SwgBlocks.Stone.LightPourstone,
				                               null, SwgBlocks.Stone.LightPourstone, SwgBlocks.Stone.LightPourstone,
				                               SwgBlocks.Stone.LightPourstone, SwgBlocks.Stone.LightPourstone, SwgBlocks.Stone.LightPourstone)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.LightPourstoneSlab, 6))
				                      .grid3x1(null,
				                               SwgBlocks.Stone.LightPourstone, SwgBlocks.Stone.LightPourstone, SwgBlocks.Stone.LightPourstone)
				                      .build(assets);
			}
		}

		//Ilum Blocks
		{
			//Ilum Stone
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumStairs, 6))
				                      .grid3x3("crafting_left",
				                               SwgBlocks.Stone.Ilum, null, null,
				                               SwgBlocks.Stone.Ilum, SwgBlocks.Stone.Ilum, null,
				                               SwgBlocks.Stone.Ilum, SwgBlocks.Stone.Ilum, SwgBlocks.Stone.Ilum)
				                      .grid3x3("crafting_right",
				                               null, null, SwgBlocks.Stone.Ilum,
				                               null, SwgBlocks.Stone.Ilum, SwgBlocks.Stone.Ilum,
				                               SwgBlocks.Stone.Ilum, SwgBlocks.Stone.Ilum, SwgBlocks.Stone.Ilum)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumSlab, 6))
				                      .grid3x1(null,
				                               SwgBlocks.Stone.Ilum, SwgBlocks.Stone.Ilum, SwgBlocks.Stone.Ilum)
				                      .build(assets);
			}

			//Smooth Ilum Stone
			{
				RecipeGenerator.Cooking.smelting("smelting", SwgBlocks.Stone.Ilum, SwgBlocks.Stone.IlumSmooth).experience(0.1F)
				                       .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumSmoothSlab, 6))
				                      .grid3x1(null,
				                               SwgBlocks.Stone.IlumSmooth, SwgBlocks.Stone.IlumSmooth, SwgBlocks.Stone.IlumSmooth)
				                      .build(assets);
			}

			//Ilum Stone Bricks
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumBricks, 4))
				                      .fill2x2(null, SwgBlocks.Stone.Ilum)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumBrickStairs, 6))
				                      .grid3x3("crafting_left",
				                               SwgBlocks.Stone.IlumBricks, null, null,
				                               SwgBlocks.Stone.IlumBricks, SwgBlocks.Stone.IlumBricks, null,
				                               SwgBlocks.Stone.IlumBricks, SwgBlocks.Stone.IlumBricks, SwgBlocks.Stone.IlumBricks)
				                      .grid3x3("crafting_right",
				                               null, null, SwgBlocks.Stone.IlumBricks,
				                               null, SwgBlocks.Stone.IlumBricks, SwgBlocks.Stone.IlumBricks,
				                               SwgBlocks.Stone.IlumBricks, SwgBlocks.Stone.IlumBricks, SwgBlocks.Stone.IlumBricks)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumBrickSlab, 6))
				                      .grid3x1(null,
				                               SwgBlocks.Stone.IlumBricks, SwgBlocks.Stone.IlumBricks, SwgBlocks.Stone.IlumBricks)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.IlumChiseledBricks))
				                      .fill1x2(null, SwgBlocks.Stone.IlumBrickSlab)
				                      .build(assets);
			}
		}

		//Massassi Blocks
		{
			//Massassi Stone
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.MassassiStairs, 6))
				                      .grid3x3("crafting_left",
				                               SwgBlocks.Stone.Massassi, null, null,
				                               SwgBlocks.Stone.Massassi, SwgBlocks.Stone.Massassi, null,
				                               SwgBlocks.Stone.Massassi, SwgBlocks.Stone.Massassi, SwgBlocks.Stone.Massassi)
				                      .grid3x3("crafting_right",
				                               null, null, SwgBlocks.Stone.Massassi,
				                               null, SwgBlocks.Stone.Massassi, SwgBlocks.Stone.Massassi,
				                               SwgBlocks.Stone.Massassi, SwgBlocks.Stone.Massassi, SwgBlocks.Stone.Massassi)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.MassassiSlab, 6))
				                      .grid3x1(null,
				                               SwgBlocks.Stone.Massassi, SwgBlocks.Stone.Massassi, SwgBlocks.Stone.Massassi)
				                      .build(assets);
			}
			//Smooth Massassi Stone
			{
				RecipeGenerator.Cooking.smelting("smelting", SwgBlocks.Stone.Massassi, SwgBlocks.Stone.MassassiSmooth).experience(0.1F)
				                       .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.MassassiSmoothSlab, 6))
				                      .grid3x1(null,
				                               SwgBlocks.Stone.MassassiSmooth, SwgBlocks.Stone.MassassiSmooth, SwgBlocks.Stone.MassassiSmooth)
				                      .build(assets);
			}
			//Massassi Stone Bricks
			{
				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.MassassiBricks, 4))
				                      .fill2x2(null, SwgBlocks.Stone.Massassi)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.MassassiBrickStairs, 6))
				                      .grid3x3("crafting_left",
				                               SwgBlocks.Stone.MassassiBricks, null, null,
				                               SwgBlocks.Stone.MassassiBricks, SwgBlocks.Stone.MassassiBricks, null,
				                               SwgBlocks.Stone.MassassiBricks, SwgBlocks.Stone.MassassiBricks, SwgBlocks.Stone.MassassiBricks)
				                      .grid3x3("crafting_right",
				                               null, null, SwgBlocks.Stone.MassassiBricks,
				                               null, SwgBlocks.Stone.MassassiBricks, SwgBlocks.Stone.MassassiBricks,
				                               SwgBlocks.Stone.MassassiBricks, SwgBlocks.Stone.MassassiBricks, SwgBlocks.Stone.MassassiBricks)
				                      .build(assets);

				RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.MassassiBrickSlab, 6))
				                      .grid3x1(null,
				                               SwgBlocks.Stone.MassassiBricks, SwgBlocks.Stone.MassassiBricks, SwgBlocks.Stone.MassassiBricks)
				                      .build(assets);

				//RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.MassassiChiseledBricks))
				//                     .fill1x2(null, SwgBlocks.Stone.MassassiBrickSlab)
				//                     .build(assets);
			}
		}

		//Light Blocks
		{
			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.Fixture, 3))
			                      .grid3x3(null,
			                               Items.GLASS_PANE, Items.GLASS_PANE, Items.GLASS_PANE,
			                               Items.GLASS_PANE, SwgItems.CraftingComponents.LightPanel, Items.GLASS_PANE,
			                               Items.GLASS_PANE, Items.GLASS_PANE, Items.GLASS_PANE)
			                      .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.RedHangar, 4))
			                      .grid3x3(null,
			                               null, SwgItems.Ingot.Plasteel, null,
			                               SwgItems.Ingot.Plasteel, SwgItems.CraftingComponents.LightPanel, SwgItems.Ingot.Plasteel,
			                               null, Items.RED_DYE, null)
			                      .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.BlueHangar, 4))
			                      .grid3x3(null,
			                               null, SwgItems.Ingot.Plasteel, null,
			                               SwgItems.Ingot.Plasteel, SwgItems.CraftingComponents.LightPanel, SwgItems.Ingot.Plasteel,
			                               null, Items.BLUE_DYE, null)
			                      .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.WallCluster, 4))
			                      .grid3x3(null,
			                               null, SwgItems.Ingot.Plasteel, null,
			                               SwgItems.Ingot.Plasteel, SwgItems.CraftingComponents.LightPanel, SwgItems.Ingot.Plasteel,
			                               null, SwgItems.Ingot.Plasteel, null)
			                      .build(assets);
		}

		//Decoration Blocks and Machines
		{
			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Door.TatooineHome))
			                      .grid2x3(null,
			                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel,
			                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel,
			                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
			                      .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Vent.Air, 3))
			                      .grid3x3(null,
			                               SwgItems.Ingot.Plasteel, SwgItems.Nugget.Plasteel, SwgItems.Ingot.Plasteel,
			                               SwgItems.Ingot.Plasteel, SwgItems.Nugget.Plasteel, SwgItems.Ingot.Plasteel,
			                               SwgItems.Ingot.Plasteel, SwgItems.Nugget.Plasteel, SwgItems.Ingot.Plasteel)
			                      .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Pipe.Large))
			                      .grid3x3(null,
			                               SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel,
			                               null, null, null,
			                               SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel, SwgItems.Ingot.Durasteel)
			                      .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Barrel.Desh))
			                      .grid3x3(null,
			                               SwgItems.Ingot.Desh, null, SwgItems.Ingot.Desh,
			                               SwgItems.Ingot.Desh, null, SwgItems.Ingot.Desh,
			                               SwgItems.Ingot.Desh, SwgItems.Ingot.Desh, SwgItems.Ingot.Desh)
			                      .build(assets);
		}

		//Crates
		{
			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.OrangeKyber))
			                      .grid3x3(null,
			                               SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium,
			                               SwgItems.Ingot.Titanium, Items.ORANGE_DYE, SwgItems.Ingot.Titanium,
			                               SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium)
			                      .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.GrayKyber))
			                      .grid3x3(null,
			                               SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium,
			                               SwgItems.Ingot.Titanium, Items.GRAY_DYE, SwgItems.Ingot.Titanium,
			                               SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium)
			                      .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.BlackKyber))
			                      .grid3x3(null,
			                               SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium,
			                               SwgItems.Ingot.Titanium, Items.BLACK_DYE, SwgItems.Ingot.Titanium,
			                               SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium, SwgItems.Ingot.Titanium)
			                      .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.Toolbox))
			                      .grid3x3(null,
			                               SwgItems.Ingot.Desh, SwgItems.Ingot.Desh, SwgItems.Ingot.Desh,
			                               SwgItems.Ingot.Desh, null, SwgItems.Ingot.Desh,
			                               Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT)
			                      .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.Imperial))
			                      .grid3x3(null,
			                               SwgItems.Ingot.Plasteel, SwgItems.CraftingComponents.LightPanel, SwgItems.Ingot.Plasteel,
			                               SwgItems.Ingot.Plasteel, null, SwgItems.Ingot.Plasteel,
			                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
			                      .build(assets);
		}

		//Crafting Components
		{
			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.DeshCoil))
			                      .grid3x3(null,
			                               SwgItems.CraftingComponents.DeshWire, SwgItems.CraftingComponents.DeshWire, SwgItems.CraftingComponents.DeshWire,
			                               SwgItems.CraftingComponents.DeshWire, null, SwgItems.CraftingComponents.DeshWire,
			                               SwgItems.CraftingComponents.DeshWire, SwgItems.CraftingComponents.DeshWire, SwgItems.CraftingComponents.DeshWire)
			                      .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.DeshWire, 9))
			                      .grid3x1("ingot",
			                               SwgItems.Ingot.Desh, SwgItems.Ingot.Desh, SwgItems.Ingot.Desh)
			                      .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.DeshWire))
			                      .grid3x1("nugget",
			                               SwgItems.Nugget.Desh, SwgItems.Nugget.Desh, SwgItems.Nugget.Desh)
			                      .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.DurasteelRod))
			                      .fill1x3(null, SwgItems.Ingot.Durasteel)
			                      .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.PlasteelRod))
			                      .fill1x3(null, SwgItems.Ingot.Plasteel)
			                      .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.ZersiumRod))
			                      .fill1x3(null, SwgItems.Ingot.Zersium)
			                      .build(assets);

			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.BallBearing))
			                      .grid3x3(null,
			                               SwgItems.Nugget.Durasteel, SwgItems.Nugget.Durasteel, SwgItems.Nugget.Durasteel,
			                               SwgItems.Nugget.Durasteel, null, SwgItems.Nugget.Durasteel,
			                               SwgItems.Nugget.Durasteel, SwgItems.Nugget.Durasteel, SwgItems.Nugget.Durasteel)
			                      .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.ElectricMotor))
			                      .grid3x3(null,
			                               SwgItems.CraftingComponents.DeshCoil, SwgItems.CraftingComponents.DurasteelRod, SwgItems.CraftingComponents.DeshCoil,
			                               SwgItems.CraftingComponents.DeshCoil, SwgItems.CraftingComponents.BallBearing, SwgItems.CraftingComponents.DeshCoil,
			                               SwgItems.CraftingComponents.DeshCoil, SwgItems.CraftingComponents.DurasteelRod, SwgItems.CraftingComponents.DeshCoil)
			                      .build(assets);
			RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.Turbine))
			                      .grid3x3(null,
			                               SwgItems.Ingot.Durasteel, null, SwgItems.Ingot.Durasteel,
			                               null, SwgItems.CraftingComponents.BallBearing, null,
			                               SwgItems.Ingot.Durasteel, null, SwgItems.Ingot.Durasteel)
			                      .build(assets);
		}
	}

	private static void generateItems(List<BuiltAsset> assets)
	{
		ItemGenerator.basic(SwgItems.Axe.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Axe.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Axe.Beskar).build(assets);

		ItemGenerator.empty(SwgItems.Blaster.Blaster).build(assets);
		ItemGenerator.basic(SwgItems.Blaster.SmallPowerPack).build(assets);

		ItemGenerator.basic(SwgItems.CraftingComponents.ElectricMotor).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.LightPanel).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.DisplayPanel).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.Turbine).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.BallBearing).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.DeshWire).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.DeshCoil).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.PlasteelRod).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.ZersiumRod).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.DurasteelRod).build(assets);
		ItemGenerator.basic(SwgItems.CraftingComponents.StrippedJaporBranch).build(assets);

		ItemGenerator.basic(SwgItems.Crystal.Exonium).build(assets);
		ItemGenerator.basic(SwgItems.Crystal.Helicite).build(assets);
		ItemGenerator.basic(SwgItems.Crystal.Ionite).build(assets);
		ItemGenerator.basic(SwgItems.Crystal.Lommite).build(assets);
		ItemGenerator.basic(SwgItems.Crystal.Thorilide).build(assets);

		ItemGenerator.basic(SwgItems.Debug.Debug).build(assets);

		BlockGenerator.basicRandomRotation(SwgBlocks.Dirt.DesertLoam).build(assets);
		BlockGenerator.basic(SwgBlocks.Dirt.WetPourstone).build(assets);
		BlockGenerator.stairs(SwgBlocks.Dirt.WetPourstoneStairs, Resources.id("block/wet_pourstone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Dirt.WetPourstoneSlab, Resources.id("block/wet_pourstone")).build(assets);
		BlockGenerator.basic(SwgBlocks.Dirt.RuinedWetPourstone).build(assets);
		BlockGenerator.stairs(SwgBlocks.Dirt.RuinedWetPourstoneStairs, Resources.id("block/ruined_wet_pourstone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Dirt.RuinedWetPourstoneSlab, Resources.id("block/ruined_wet_pourstone")).build(assets);

		ItemGenerator.basic(SwgItems.Door.TatooineHome).build(assets);

		ItemGenerator.basic(SwgItems.Dust.Ionite).build(assets);

		ItemGenerator.basic(SwgItems.Food.SaltPile).build(assets);
		ItemGenerator.basic(SwgItems.Food.JoganFruit).build(assets);
		ItemGenerator.basic(SwgItems.Food.ChasukaLeaf).build(assets);
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
		ItemGenerator.basic(SwgItems.Food.BlueYogurt).build(assets);

		ItemGenerator.basic(SwgItems.Food.QrikkiBread).build(assets);
		ItemGenerator.basic(SwgItems.Food.QrikkiWaffle).build(assets);

		ItemGenerator.basic(SwgItems.Food.AhrisaBowl).build(assets);
		ItemGenerator.basic(SwgItems.Food.BlackMelon).build(assets);
		ItemGenerator.basic(SwgItems.Food.ChasukaSeeds).build(assets);
		ItemGenerator.basic(SwgItems.Food.DesertPlums).build(assets);
		ItemGenerator.basic(SwgItems.Food.DriedPoontenGrass).build(assets);
		ItemGenerator.basic(SwgItems.Food.HarounBread).build(assets);
		ItemGenerator.basic(SwgItems.Food.HkakBean).build(assets);
		ItemGenerator.basic(SwgItems.Food.PallieFruit).build(assets);
		ItemGenerator.basic(SwgItems.Food.PikaFruit).build(assets);
		ItemGenerator.basic(SwgItems.Food.Tuber).build(assets);
		ItemGenerator.basic(SwgItems.Food.CookedEopieLoin).build(assets);
		ItemGenerator.basic(SwgItems.Food.CrispyGorg).build(assets);
		ItemGenerator.basic(SwgItems.Food.DebDeb).build(assets);
		ItemGenerator.basic(SwgItems.Food.DewbackEgg).build(assets);
		ItemGenerator.basic(SwgItems.Food.DewbackOmlette).build(assets);
		ItemGenerator.basic(SwgItems.Food.EopieLoin).build(assets);
		ItemGenerator.basic(SwgItems.Food.HubbaGourd).build(assets);
		ItemGenerator.basic(SwgItems.Food.JerbaRack).build(assets);
		ItemGenerator.basic(SwgItems.Food.JerbaRib).build(assets);
		ItemGenerator.basic(SwgItems.Food.KraytMeat).build(assets);
		ItemGenerator.basic(SwgItems.Food.RawSkettoNugget).build(assets);
		ItemGenerator.basic(SwgItems.Food.RoastKrayt).build(assets);
		ItemGenerator.basic(SwgItems.Food.RontoChuck).build(assets);
		ItemGenerator.basic(SwgItems.Food.TuberMash).build(assets);
		ItemGenerator.basic(SwgItems.Food.VaporatorMushroom).build(assets);
		ItemGenerator.basic(SwgItems.Food.WorrtEgg).build(assets);

		ItemGenerator.basic(SwgItems.MobDrops.FaaBucket).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.LaaBucket).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.CorpseOfGorg).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.BanthaHorn).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.DewbackBone).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.DewbackBoneShard).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.EyeOfSketto).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.Hide).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.KraytPearl).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.KraytTooth).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.KreetleHusk).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.LizardGizzard).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.SquillLiver).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.TongueOfWorrt).build(assets);
		ItemGenerator.basic(SwgItems.MobDrops.ToughHide).build(assets);

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

		ItemGenerator.basic(SwgItems.RawOre.Beskar).build(assets);
		ItemGenerator.basic(SwgItems.RawOre.Chromium).build(assets);
		ItemGenerator.basic(SwgItems.RawOre.Cortosis).build(assets);
		ItemGenerator.basic(SwgItems.RawOre.Desh).build(assets);
		ItemGenerator.basic(SwgItems.RawOre.Diatium).build(assets);
		ItemGenerator.basic(SwgItems.RawOre.Kelerium).build(assets);
		ItemGenerator.basic(SwgItems.RawOre.Rubindum).build(assets);
		ItemGenerator.basic(SwgItems.RawOre.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.RawOre.Zersium).build(assets);

		ItemGenerator.basic(SwgItems.Pickaxe.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Pickaxe.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Pickaxe.Beskar).build(assets);

		ItemGenerator.basic(SwgItems.Shovel.Durasteel).build(assets);
		ItemGenerator.basic(SwgItems.Shovel.Titanium).build(assets);
		ItemGenerator.basic(SwgItems.Shovel.Beskar).build(assets);

		ItemGenerator.basic(SwgItems.Spawners.XwingT65b).build(assets);

		ItemGenerator.spawn_egg(SwgItems.Spawners.Faa).build(assets);
		ItemGenerator.spawn_egg(SwgItems.Spawners.Laa).build(assets);
		ItemGenerator.spawn_egg(SwgItems.Spawners.Worrt).build(assets);
	}

	private static void generateBlocks(List<BuiltAsset> assets)
	{
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Barrel.Desh).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.OrangeKyber).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.GrayKyber).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.BlackKyber).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.Toolbox).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.Imperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Crate.Segmented).build(assets);

		BlockGenerator.particleOnly(SwgBlocks.Door.TatooineHomeTop, new Identifier("block/stone")).build(assets);
		BlockGenerator.particleOnly(SwgBlocks.Door.TatooineHomeBottom, new Identifier("block/stone")).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Light.RedHangar).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Light.BlueHangar).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Light.WallCluster).build(assets);

		BlockGenerator.basic(SwgBlocks.Light.Fixture)
		              .state((block, modelId) -> BlockStateGenerator.forBooleanProperty(block, InvertedLampBlock.LIT, IdentifierUtil.concat(modelId, "_on"), modelId))
		              .models(block -> ModelFile.cubes(block, "", "_on"))
		              .itemModel(block -> ModelFile.ofBlockDifferentParent(block, IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_on")))
		              .build(assets);

		BlockGenerator.leaves(SwgBlocks.Leaves.Sequoia).build(assets);
		BlockGenerator.tangentFan(SwgBlocks.Leaves.Japor).build(assets);

		BlockGenerator.column(SwgBlocks.Log.Sequoia, Resources.id("block/sequoia_log_top"), Resources.id("block/sequoia_log")).build(assets);
		BlockGenerator.column(SwgBlocks.Log.Japor, Resources.id("block/japor_log_top"), Resources.id("block/japor_log")).build(assets);
		BlockGenerator.column(SwgBlocks.Log.Tatooine, Resources.id("block/tatooine_log_top"), Resources.id("block/tatooine_log")).build(assets);

		BlockGenerator.column(SwgBlocks.Wood.Sequoia, Resources.id("block/sequoia_log"), Resources.id("block/sequoia_log")).build(assets);
		BlockGenerator.column(SwgBlocks.Wood.Japor, Resources.id("block/japor_log"), Resources.id("block/japor_log")).build(assets);
		BlockGenerator.column(SwgBlocks.Wood.Tatooine, Resources.id("block/tatooine_log"), Resources.id("block/tatooine_log")).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Machine.Spoked).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.Imperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.WhiteStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.OrangeStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.MagentaStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.LightBlueStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.YellowStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.LimeStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.PinkStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.GrayStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.LightGrayStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.CyanStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.PurpleStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.BlueStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.BrownStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.GreenStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.RedStainedImperial).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Glass.BlackStainedImperial).build(assets);

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
		BlockGenerator.block(SwgBlocks.MaterialBlock.Ionite).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.MoistureVaporator.Gx8)
		              .itemModel(ModelFile::blockSeparateItem).build(assets);

		// TODO: adjust loot table to match vanilla raw ores
		BlockGenerator.basic(SwgBlocks.Ore.Beskar)
		              .lootTable(block -> LootTableFile.singleFortuneBonus(block, SwgItems.RawOre.Beskar))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Chromium)
		              .lootTable(block -> LootTableFile.singleFortuneBonus(block, SwgItems.RawOre.Chromium))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Cortosis)
		              .lootTable(block -> LootTableFile.singleFortuneBonus(block, SwgItems.RawOre.Cortosis))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Desh)
		              .lootTable(block -> LootTableFile.singleFortuneBonus(block, SwgItems.RawOre.Desh))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Diatium)
		              .lootTable(block -> LootTableFile.singleFortuneBonus(block, SwgItems.RawOre.Diatium))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Lommite)
		              .lootTable(block -> LootTableFile.singleFortuneBonus(block, SwgItems.Crystal.Lommite))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Titanium)
		              .lootTable(block -> LootTableFile.singleFortuneBonus(block, SwgItems.RawOre.Titanium))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Zersium)
		              .lootTable(block -> LootTableFile.singleFortuneBonus(block, SwgItems.RawOre.Zersium))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Ionite)
		              .lootTable(block -> LootTableFile.many(block, SwgItems.Crystal.Ionite, new LootTableFile.Pool.Entry.CountFunction.Range(1, 3, new Identifier("uniform"))))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Helicite)
		              .lootTable(block -> LootTableFile.many(block, SwgItems.Crystal.Helicite, new LootTableFile.Pool.Entry.CountFunction.Range(1, 3, new Identifier("uniform"))))
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Ore.Thorilide)
		              .lootTable(block -> LootTableFile.many(block, SwgItems.Crystal.Thorilide, new LootTableFile.Pool.Entry.CountFunction.Range(1, 3, new Identifier("uniform"))))
		              .build(assets);

		BlockGenerator.basic(SwgBlocks.Panel.BlackImperialPanelBlank).build(assets);
		BlockGenerator.basic(SwgBlocks.Panel.GrayImperialPanelBlank).build(assets);

		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf1, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf2, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf3, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf4, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf5, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightOff1, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightOff2, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightOn1, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightOn2, Resources.id("block/gray_imperial_panel_blank")).build(assets);

		BlockGenerator.column(SwgBlocks.Panel.GrayImperialPanelPattern1, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialPanelPattern2, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialPanelPattern3, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialPanelPattern4, Resources.id("block/gray_imperial_panel_blank")).build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialPanelPattern5, Resources.id("block/gray_imperial_panel_blank")).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Panel.ImperialLightTall1).build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Panel.ImperialLightTall2).build(assets);

		BlockGenerator.basic(SwgBlocks.Panel.LabWall).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Pipe.Large)
		              .itemModel(ModelFile::blockSeparateItem).build(assets);

		BlockGenerator.cross(SwgBlocks.Plant.FunnelFlower).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.BlossomingFunnelFlower).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.PoontenGrass).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.DriedPoontenGrass).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.Tuber).build(assets);

		BlockGenerator.cropStages(SwgBlocks.Plant.Chasuka, SwgBlocks.Plant.Chasuka::getAgeProperty, IdentifierUtil.concat(AssetGenerator.getTextureName(SwgBlocks.Plant.Chasuka), "_stage2")).build(assets);
		BlockGenerator.bushStages(SwgBlocks.Plant.HkakBush, () -> HkakBushBlock.AGE, IdentifierUtil.concat(AssetGenerator.getTextureName(SwgBlocks.Plant.HkakBush), "_stage3")).build(assets);

		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.SaltyDesert).build(assets);
		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.Desert).build(assets);
		BlockGenerator.basicRandomRotation(SwgBlocks.Sand.DesertCanyon).build(assets);

		BlockGenerator.basicRandomRotation(SwgBlocks.Salt.Caked).build(assets);

		BlockGenerator.staticColumn(SwgBlocks.Stone.DesertSediment, Resources.id("block/desert_sediment_top"), Resources.id("block/desert_sediment")).build(assets);

		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.Massassi).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.MassassiSlab, Resources.id("block/massassi_stone")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.MassassiStairs, Resources.id("block/massassi_stone")).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.MassassiSmooth).build(assets);
		BlockGenerator.slabUniqueDouble(SwgBlocks.Stone.MassassiSmoothSlab, Resources.id("block/smooth_massassi_stone_slab_double"), Resources.id("block/smooth_massassi_stone"), Resources.id("block/smooth_massassi_stone_slab_side")).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.MassassiBricks).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.MassassiBrickSlab, Resources.id("block/massassi_stone_bricks")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.MassassiBrickStairs, Resources.id("block/massassi_stone_bricks")).build(assets);
		//BlockGenerator.basic(SwgBlocks.Stone.MassassiChiseledBricks).build(assets);

		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.Pourstone).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.PourstoneStairs, Resources.id("block/pourstone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.PourstoneSlab, Resources.id("block/pourstone")).build(assets);
		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.CrackedPourstone).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.CrackedPourstoneStairs, Resources.id("block/cracked_pourstone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.CrackedPourstoneSlab, Resources.id("block/cracked_pourstone")).build(assets);
		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.LightPourstone).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.LightPourstoneStairs, Resources.id("block/light_pourstone")).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.LightPourstoneSlab, Resources.id("block/light_pourstone")).build(assets);

		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.Ilum).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.IlumSlab, Resources.id("block/ilum_stone")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.IlumStairs, Resources.id("block/ilum_stone")).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.IlumSmooth).build(assets);
		BlockGenerator.slabUniqueDouble(SwgBlocks.Stone.IlumSmoothSlab, Resources.id("block/smooth_ilum_stone_slab_double"), Resources.id("block/smooth_ilum_stone"), Resources.id("block/smooth_ilum_stone_slab_side")).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.IlumBricks).build(assets);
		BlockGenerator.slab(SwgBlocks.Stone.IlumBrickSlab, Resources.id("block/ilum_stone_bricks")).build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.IlumBrickStairs, Resources.id("block/ilum_stone_bricks")).build(assets);
		BlockGenerator.basic(SwgBlocks.Stone.IlumChiseledBricks).build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Tank.Fusion).build(assets);

		BlockGenerator.staticColumn(SwgBlocks.Vent.Air, Resources.id("block/air_vent_top"), Resources.id("block/air_vent_side")).build(assets);

		BlockGenerator.basic(SwgBlocks.Workbench.Blaster).build(assets);
		BlockGenerator.basic(SwgBlocks.Workbench.Lightsaber).build(assets);
	}
}
