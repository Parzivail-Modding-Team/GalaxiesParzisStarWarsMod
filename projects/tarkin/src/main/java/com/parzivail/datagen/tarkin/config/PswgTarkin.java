package com.parzivail.datagen.tarkin.config;

import com.parzivail.datagen.tarkin.*;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Config;
import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.block.crop.HkakBushBlock;
import com.parzivail.pswg.block.crop.MoloShrubBlock;
import com.parzivail.pswg.character.SpeciesVariable;
import com.parzivail.pswg.client.screen.CharacterScreen;
import com.parzivail.pswg.client.species.SwgSpeciesLore;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.data.SwgSpeciesManager;
import com.parzivail.pswg.features.blasters.BlasterItem;
import com.parzivail.pswg.features.blasters.client.workbench.BlasterWorkbenchScreen;
import com.parzivail.pswg.features.blasters.data.BlasterArchetype;
import com.parzivail.pswg.features.blasters.data.BlasterFiringMode;
import com.parzivail.pswg.features.lightsabers.LightsaberItem;
import com.parzivail.util.block.InvertedLampBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

import java.util.List;

public class PswgTarkin
{
	public static void generateLangEntries(List<BuiltAsset> assets)
	{
		var lang = new LanguageBuilder(Resources.id(LanguageProvider.OUTPUT_LOCALE));

		// Commands
		var speciesCmd = lang.command("species");
		speciesCmd.dot("invalid").build(assets);
		speciesCmd.dot("variant").dot("invalid").build(assets);

		// Deaths
		var deathBlaster = lang.cause_of_death("blaster");
		deathBlaster.build(assets);
		deathBlaster.dot("slug").build(assets);

		// REI categories
		var reiVaporator = lang.category("vaporator");
		reiVaporator.build(assets);
		reiVaporator.dot("time").build(assets);

		// Containers
		lang.container("blaster_workbench").build(assets);

		lang.container("corrugated_crate").build(assets);
		lang.container("kyber_crate").build(assets);
		lang.container("lightsaber_forge").build(assets);

		var gx8 = lang.container("moisture_vaporator_gx8");
		gx8.build(assets);

		lang.container("mos_eisley_crate").build(assets);
		lang.container("segmented_crate").build(assets);
		lang.container("toolbox").build(assets);

		// Entities
		lang.entity(SwgEntities.Misc.BlasterBolt).build(assets);
		lang.entity(SwgEntities.Misc.ThrownLightsaber).build(assets);
		lang.entity(SwgEntities.Ship.T65bXwing).build(assets);
		lang.entity(SwgEntities.Speeder.X34).build(assets);
		lang.entity(SwgEntities.Speeder.ZephyrJ).build(assets);
		lang.entity(SwgEntities.Fish.Faa).build(assets);
		lang.entity(SwgEntities.Fish.Laa).build(assets);
		lang.entity(SwgEntities.Amphibian.Worrt).build(assets);
		lang.entity(SwgEntities.Mammal.Bantha).build(assets);
		lang.entity(SwgEntities.Rodent.SandSkitter).build(assets);
		lang.entity(SwgEntities.Droid.AstroR2D2).build(assets);
		lang.entity(SwgEntities.Droid.AstroR2Q5).build(assets);
		lang.entity(SwgEntities.Droid.AstroR2KP).build(assets);
		lang.entity(SwgEntities.Droid.AstroR2R7).build(assets);
		lang.entity(SwgEntities.Droid.AstroR2Y10).build(assets);
		lang.entity(SwgEntities.Droid.AstroQTKT).build(assets);

		Tarkin.registerLangFields(Resources.class, lang, assets);
		Tarkin.registerLangFields(CharacterScreen.class, lang, assets);
		Tarkin.registerLangFields(BlasterItem.class, lang, assets);
		Tarkin.registerLangFields(LightsaberItem.class, lang, assets);
		Tarkin.registerLangFields(BlasterWorkbenchScreen.class, lang, assets);

		// Item
		lang.item("lightsaber").dot("darksaber").build(assets);

		// Lore
		lang.lore(SwgItems.Food.Kreetlejuice).build(assets);
		lang.status(SwgItems.Cable.Power).build(assets);

		// Item groups
		lang.itemGroup(Galaxies.TabBlocks).build(assets);
		lang.itemGroup(Galaxies.TabItems).build(assets);
		lang.itemGroup(Galaxies.TabBlasters).build(assets);
		lang.itemGroup(Galaxies.TabLightsabers).build(assets);

		// Key categories
		lang.keyCategory("pswg").build(assets);

		// Keys
		lang.key(Client.KEY_PRIMARY_ITEM_ACTION).build(assets);
		lang.key(Client.KEY_SECONDARY_ITEM_ACTION).build(assets);
		lang.key(Client.KEY_SHIP_INPUT_MODE_OVERRIDE).build(assets);
		lang.key(Client.KEY_SPECIES_SELECT).build(assets);

		// Messages
		lang.message("update").build(assets);

		lang.message("tip").dot("customize_character").build(assets);

		for (var value : BlasterFiringMode.values())
			lang.entry(value.getTranslation()).build(assets);

		// Species
		generateSpeciesLang(assets, lang, Resources.MODID);

		// Blaster attachments
		generateBlasterLang(assets, lang, Resources.MODID);

		// Autoconfig
		generateConfigLang(assets, lang, Config.class);
	}

	public static void generateTags(List<BuiltAsset> assets)
	{
		TagGenerator.forBlockTag(BlockTags.LOGS_THAT_BURN, SwgTags.Block.SEQUOIA_LOG).build(assets);
		TagGenerator.forItemTag(ItemTags.LOGS_THAT_BURN, SwgTags.Block.SEQUOIA_LOG).build(assets);

		TagGenerator.forBlockTag(BlockTags.LOGS_THAT_BURN, SwgTags.Block.JAPOR_LOG).build(assets);
		TagGenerator.forItemTag(ItemTags.LOGS_THAT_BURN, SwgTags.Block.JAPOR_LOG).build(assets);

		TagGenerator.forBlockTag(BlockTags.LOGS_THAT_BURN, SwgTags.Block.TATOOINE_LOG).build(assets);
		TagGenerator.forItemTag(ItemTags.LOGS_THAT_BURN, SwgTags.Block.TATOOINE_LOG).build(assets);

		TagGenerator.forBlockTag(BlockTags.SAND, SwgTags.Block.DESERT_SAND).build(assets);
		TagGenerator.forBlockTag(BlockTags.SHOVEL_MINEABLE, SwgTags.Block.DESERT_SAND).build(assets);
		TagGenerator.forItemTag(ItemTags.SAND, SwgTags.Block.DESERT_SAND).build(assets);

		TagGenerator.forBlockTag(BlockTags.WALLS, SwgTags.Block.SLIDING_DOORS).build(assets);
	}

	public static void generateRecipes(List<BuiltAsset> assets)
	{
		//Metals
		RecipeGenerator.buildMetal(
				assets,
				5.0F,
				SwgBlocks.Ore.BeskarOre,
				SwgItems.Material.BeskarRaw,
				SwgItems.Material.BeskarIngot,
				SwgBlocks.Ore.BeskarBlock
		);

		RecipeGenerator.buildMetal(
				assets,
				1.0F,
				SwgBlocks.Ore.ChromiumOre,
				SwgItems.Material.ChromiumRaw,
				SwgItems.Material.ChromiumIngot,
				SwgItems.Material.ChromiumNugget,
				SwgBlocks.Ore.ChromiumBlock
		);

		RecipeGenerator.buildMetal(
				assets,
				4.0F,
				SwgBlocks.Ore.CortosisOre,
				SwgItems.Material.CortosisRaw,
				SwgItems.Material.CortosisIngot,
				SwgBlocks.Ore.CortosisBlock
		);

		RecipeGenerator.buildMetal(
				assets,
				0.4F,
				SwgBlocks.Ore.DeshOre,
				SwgItems.Material.DeshRaw,
				SwgItems.Material.DeshIngot,
				SwgItems.Material.DeshNugget,
				SwgBlocks.Ore.DeshBlock
		);

		RecipeGenerator.buildMetal(
				assets,
				0.7F,
				SwgBlocks.Ore.DiatiumOre,
				SwgItems.Material.DiatiumRaw,
				SwgItems.Material.DiatiumIngot,
				SwgItems.Material.DiatiumNugget,
				SwgBlocks.Ore.DiatiumBlock
		);

		// TODO: durasteel

		// TODO: plasteel

		RecipeGenerator.buildMetal(
				assets,
				2.0F,
				SwgBlocks.Ore.TitaniumOre,
				SwgItems.Material.TitaniumRaw,
				SwgItems.Material.TitaniumIngot,
				SwgItems.Material.TitaniumNugget,
				SwgBlocks.Ore.TitaniumBlock
		);

		RecipeGenerator.buildMetal(
				assets,
				2.0F,
				SwgBlocks.Ore.IoniteOre,
				SwgItems.Material.IoniteRaw,
				SwgItems.Material.IoniteIngot,
				SwgItems.Material.IoniteNugget,
				SwgBlocks.Ore.IoniteBlock
		);

		// Crystals

		RecipeGenerator.buildCrystal(
				assets,
				SwgItems.Material.HeliciteDust,
				SwgItems.Material.HeliciteCrystal,
				SwgBlocks.Ore.HeliciteBlock
		);

		RecipeGenerator.buildCrystal(
				assets,
				SwgItems.Material.LommiteDust,
				SwgItems.Material.LommiteCrystal,
				SwgBlocks.Ore.LommiteBlock
		);

		RecipeGenerator.buildCrystal(
				assets,
				SwgItems.Material.ThorilideDust,
				SwgItems.Material.ThorilideCrystal,
				SwgBlocks.Ore.ThorilideBlock
		);

		RecipeGenerator.buildCrystal(
				assets,
				SwgItems.Material.ZersiumDust,
				SwgItems.Material.ZersiumCrystal,
				SwgBlocks.Ore.ZersiumBlock
		);

		//Food
		RecipeGenerator.buildFood(assets, SwgItems.Food.BanthaChop, SwgItems.Food.BanthaSteak);
		RecipeGenerator.buildFood(assets, SwgItems.Food.GizkaChop, SwgItems.Food.GizkaSteak);
		RecipeGenerator.buildFood(assets, SwgItems.Food.MynockWing, SwgItems.Food.FriedMynockWing);
		RecipeGenerator.buildFood(assets, SwgItems.Food.NerfChop, SwgItems.Food.NerfSteak);
		RecipeGenerator.buildFood(assets, SwgItems.Food.QrikkiBread, SwgItems.Food.QrikkiWaffle);

		//Plasteel alternative recipes
		RecipeGenerator.Shaped.of(new ItemStack(Items.ANVIL))
		                      .grid3x3("plasteel",
		                               SwgBlocks.Composite.PlasteelBlock, SwgBlocks.Composite.PlasteelBlock, SwgBlocks.Composite.PlasteelBlock,
		                               null, SwgItems.Material.PlasteelIngot, null,
		                               SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.BLAST_FURNACE))
		                      .grid3x3("plasteel",
		                               SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, Items.FURNACE, SwgItems.Material.PlasteelIngot,
		                               Items.SMOOTH_STONE, Items.SMOOTH_STONE, Items.SMOOTH_STONE)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.BUCKET))
		                      .grid3x2("plasteel",
		                               SwgItems.Material.PlasteelIngot, null, SwgItems.Material.PlasteelIngot,
		                               null, SwgItems.Material.PlasteelIngot, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.CAULDRON))
		                      .grid3x3("plasteel",
		                               SwgItems.Material.PlasteelIngot, null, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, null, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.CHAIN))
		                      .grid1x3("plasteel",
		                               SwgItems.Material.PlasteelNugget,
		                               SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelNugget)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.COMPASS))
		                      .grid3x3("plasteel",
		                               null, SwgItems.Material.PlasteelIngot, null,
		                               SwgItems.Material.PlasteelIngot, Items.REDSTONE, SwgItems.Material.PlasteelIngot,
		                               null, SwgItems.Material.PlasteelIngot, null)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.SHEARS))
		                      .grid2x2("plasteel_left",
		                               SwgItems.Material.PlasteelIngot, null,
		                               null, SwgItems.Material.PlasteelIngot)
		                      .grid2x2("plasteel_right",
		                               null, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, null)
		                      .build(assets);
		RecipeGenerator.Shapeless.of(new ItemStack(Items.FLINT_AND_STEEL), "plasteel")
		                         .ingredient(SwgItems.Material.PlasteelIngot)
		                         .ingredient(Items.FLINT)
		                         .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.CROSSBOW))
		                      .grid3x3("plasteel",
		                               Items.STICK, SwgItems.Material.PlasteelIngot, Items.STICK,
		                               Items.STRING, Items.TRIPWIRE_HOOK, Items.STRING,
		                               null, Items.STICK, null)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.SHIELD))
		                      .grid3x3("plasteel",
		                               ItemTags.PLANKS, SwgItems.Material.PlasteelIngot, ItemTags.PLANKS,
		                               ItemTags.PLANKS, ItemTags.PLANKS, ItemTags.PLANKS,
		                               null, ItemTags.PLANKS, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.HEAVY_WEIGHTED_PRESSURE_PLATE))
		                      .grid2x1("plasteel", SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.HOPPER))
		                      .grid3x3("plasteel",
		                               SwgItems.Material.PlasteelIngot, null, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, Items.CHEST, SwgItems.Material.PlasteelIngot,
		                               null, SwgItems.Material.PlasteelIngot, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.MINECART))
		                      .grid3x2("plasteel",
		                               SwgItems.Material.PlasteelIngot, null, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.RAIL, 16))
		                      .grid3x3("plasteel",
		                               SwgItems.Material.PlasteelIngot, null, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, Items.STICK, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, null, SwgItems.Material.PlasteelIngot)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.ACTIVATOR_RAIL, 6))
		                      .grid3x3("plasteel",
		                               SwgItems.Material.PlasteelIngot, Items.STICK, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, Items.REDSTONE_TORCH, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, Items.STICK, SwgItems.Material.PlasteelIngot)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.DETECTOR_RAIL, 6))
		                      .grid3x3("plasteel",
		                               SwgItems.Material.PlasteelIngot, null, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, Items.STONE_PRESSURE_PLATE, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, Items.REDSTONE, SwgItems.Material.PlasteelIngot)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.PISTON))
		                      .grid3x3("plasteel",
		                               ItemTags.PLANKS, ItemTags.PLANKS, ItemTags.PLANKS,
		                               Items.COBBLESTONE, SwgItems.Material.PlasteelIngot, Items.COBBLESTONE,
		                               Items.COBBLESTONE, Items.REDSTONE, Items.COBBLESTONE)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.SMITHING_TABLE))
		                      .grid2x3("plasteel",
		                               SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelIngot,
		                               ItemTags.PLANKS, ItemTags.PLANKS,
		                               ItemTags.PLANKS, ItemTags.PLANKS)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.STONECUTTER))
		                      .grid3x2("plasteel",
		                               null, SwgItems.Material.PlasteelIngot, null,
		                               Items.STONE, Items.STONE, Items.STONE)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.TRIPWIRE_HOOK, 2))
		                      .grid1x3("plasteel",
		                               SwgItems.Material.PlasteelIngot,
		                               Items.STICK,
		                               ItemTags.PLANKS)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(Items.LANTERN))
		                      .grid3x3("plasteel",
		                               SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelNugget,
		                               SwgItems.Material.PlasteelNugget, Items.TORCH, SwgItems.Material.PlasteelNugget,
		                               SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelNugget)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(Items.SOUL_LANTERN))
		                      .grid3x3("plasteel",
		                               SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelNugget,
		                               SwgItems.Material.PlasteelNugget, Items.SOUL_TORCH, SwgItems.Material.PlasteelNugget,
		                               SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelNugget)
		                      .build(assets);

		//Tools
		//Axes
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.DurasteelAxe))
		                      .grid3x3("crafting_left",
		                               SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot, null,
		                               SwgItems.Material.DurasteelIngot, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .grid3x3("crafting_right",
		                               null, SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, SwgItems.Material.DurasteelIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.TitaniumAxe))
		                      .grid3x3("crafting_left",
		                               SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot, null,
		                               SwgItems.Material.TitaniumIngot, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .grid3x3("crafting_right",
		                               null, SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, SwgItems.Material.TitaniumIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.BeskarAxe))
		                      .grid3x3("crafting_left",
		                               SwgItems.Material.BeskarIngot, SwgItems.Material.BeskarIngot, null,
		                               SwgItems.Material.BeskarIngot, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .grid3x3("crafting_right",
		                               null, SwgItems.Material.BeskarIngot, SwgItems.Material.BeskarIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, SwgItems.Material.BeskarIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);
		//Hoes
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.DurasteelHoe))
		                      .grid3x3("crafting_left",
		                               SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .grid3x3("crafting_right",
		                               null, SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.TitaniumHoe))
		                      .grid3x3("crafting_left",
		                               SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .grid3x3("crafting_right",
		                               null, SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.BeskarHoe))
		                      .grid3x3("crafting_left",
		                               SwgItems.Material.BeskarIngot, SwgItems.Material.BeskarIngot, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .grid3x3("crafting_right",
		                               null, SwgItems.Material.BeskarIngot, SwgItems.Material.BeskarIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);
		//Pickaxes
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.DurasteelPickaxe))
		                      .grid3x3(null,
		                               SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.TitaniumPickaxe))
		                      .grid3x3(null,
		                               SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.BeskarPickaxe))
		                      .grid3x3(null,
		                               SwgItems.Material.BeskarIngot, SwgItems.Material.BeskarIngot, SwgItems.Material.BeskarIngot,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);
		//Shovels
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.DurasteelShovel))
		                      .grid3x3(null,
		                               null, SwgItems.Material.DurasteelIngot, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.TitaniumShovel))
		                      .grid3x3(null,
		                               null, SwgItems.Material.TitaniumIngot, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Material.BeskarShovel))
		                      .grid3x3(null,
		                               null, SwgItems.Material.BeskarIngot, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null,
		                               null, SwgItems.CraftingComponents.DurasteelRod, null)
		                      .build(assets);

		//Pourstone Blocks
		//Wet Pourstone
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

		//Ruined Wet Pourstone
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

		//RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Stone.MassassiChiseledBricks))
		//                     .fill1x2(null, SwgBlocks.Stone.MassassiBrickSlab)
		//                     .build(assets);

		//Light Blocks
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.Fixture, 3))
		                      .grid3x3(null,
		                               Items.GLASS_PANE, Items.GLASS_PANE, Items.GLASS_PANE,
		                               Items.GLASS_PANE, SwgItems.CraftingComponents.LightPanel, Items.GLASS_PANE,
		                               Items.GLASS_PANE, Items.GLASS_PANE, Items.GLASS_PANE)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.RedHangar, 4))
		                      .grid3x3(null,
		                               null, SwgItems.Material.PlasteelIngot, null,
		                               SwgItems.Material.PlasteelIngot, SwgItems.CraftingComponents.LightPanel, SwgItems.Material.PlasteelIngot,
		                               null, Items.RED_DYE, null)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.BlueHangar, 4))
		                      .grid3x3(null,
		                               null, SwgItems.Material.PlasteelIngot, null,
		                               SwgItems.Material.PlasteelIngot, SwgItems.CraftingComponents.LightPanel, SwgItems.Material.PlasteelIngot,
		                               null, Items.BLUE_DYE, null)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Light.WallCluster, 4))
		                      .grid3x3(null,
		                               null, SwgItems.Material.PlasteelIngot, null,
		                               SwgItems.Material.PlasteelIngot, SwgItems.CraftingComponents.LightPanel, SwgItems.Material.PlasteelIngot,
		                               null, SwgItems.Material.PlasteelIngot, null)
		                      .build(assets);

		//Decoration Blocks and Machines
		//		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.Door.TatooineHome))
		//		                      .grid2x3(null,
		//		                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel,
		//		                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel,
		//		                               SwgItems.Ingot.Plasteel, SwgItems.Ingot.Plasteel)
		//		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Vent.Air, 3))
		                      .grid3x3(null,
		                               SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelIngot,
		                               SwgItems.Material.PlasteelIngot, SwgItems.Material.PlasteelNugget, SwgItems.Material.PlasteelIngot)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Pipe.Large))
		                      .grid3x3(null,
		                               SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot,
		                               null, null, null,
		                               SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot, SwgItems.Material.DurasteelIngot)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Barrel.Desh))
		                      .grid3x3(null,
		                               SwgItems.Material.DeshIngot, null, SwgItems.Material.DeshIngot,
		                               SwgItems.Material.DeshIngot, null, SwgItems.Material.DeshIngot,
		                               SwgItems.Material.DeshIngot, SwgItems.Material.DeshIngot, SwgItems.Material.DeshIngot)
		                      .build(assets);

		//Crates
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.OrangeKyber))
		                      .grid3x3(null,
		                               SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot,
		                               SwgItems.Material.TitaniumIngot, Items.ORANGE_DYE, SwgItems.Material.TitaniumIngot,
		                               SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.GrayKyber))
		                      .grid3x3(null,
		                               SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot,
		                               SwgItems.Material.TitaniumIngot, Items.GRAY_DYE, SwgItems.Material.TitaniumIngot,
		                               SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.BlackKyber))
		                      .grid3x3(null,
		                               SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot,
		                               SwgItems.Material.TitaniumIngot, Items.BLACK_DYE, SwgItems.Material.TitaniumIngot,
		                               SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot, SwgItems.Material.TitaniumIngot)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgBlocks.Crate.Toolbox))
		                      .grid3x3(null,
		                               SwgItems.Material.DeshIngot, SwgItems.Material.DeshIngot, SwgItems.Material.DeshIngot,
		                               SwgItems.Material.DeshIngot, null, SwgItems.Material.DeshIngot,
		                               Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT)
		                      .build(assets);

		//Crafting Components
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.DeshCoil))
		                      .grid3x3(null,
		                               SwgItems.CraftingComponents.DeshWire, SwgItems.CraftingComponents.DeshWire, SwgItems.CraftingComponents.DeshWire,
		                               SwgItems.CraftingComponents.DeshWire, null, SwgItems.CraftingComponents.DeshWire,
		                               SwgItems.CraftingComponents.DeshWire, SwgItems.CraftingComponents.DeshWire, SwgItems.CraftingComponents.DeshWire)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.DeshWire, 9))
		                      .grid3x1("ingot",
		                               SwgItems.Material.DeshIngot, SwgItems.Material.DeshIngot, SwgItems.Material.DeshIngot)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.DeshWire))
		                      .grid3x1("nugget",
		                               SwgItems.Material.DeshNugget, SwgItems.Material.DeshNugget, SwgItems.Material.DeshNugget)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.DurasteelRod))
		                      .fill1x3(null, SwgItems.Material.DurasteelIngot)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.PlasteelRod))
		                      .fill1x3(null, SwgItems.Material.PlasteelIngot)
		                      .build(assets);

		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.BallBearing))
		                      .grid3x3(null,
		                               SwgItems.Material.DurasteelNugget, SwgItems.Material.DurasteelNugget, SwgItems.Material.DurasteelNugget,
		                               SwgItems.Material.DurasteelNugget, null, SwgItems.Material.DurasteelNugget,
		                               SwgItems.Material.DurasteelNugget, SwgItems.Material.DurasteelNugget, SwgItems.Material.DurasteelNugget)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.ElectricMotor))
		                      .grid3x3(null,
		                               SwgItems.CraftingComponents.DeshCoil, SwgItems.CraftingComponents.DurasteelRod, SwgItems.CraftingComponents.DeshCoil,
		                               SwgItems.CraftingComponents.DeshCoil, SwgItems.CraftingComponents.BallBearing, SwgItems.CraftingComponents.DeshCoil,
		                               SwgItems.CraftingComponents.DeshCoil, SwgItems.CraftingComponents.DurasteelRod, SwgItems.CraftingComponents.DeshCoil)
		                      .build(assets);
		RecipeGenerator.Shaped.of(new ItemStack(SwgItems.CraftingComponents.Turbine))
		                      .grid3x3(null,
		                               SwgItems.Material.DurasteelIngot, null, SwgItems.Material.DurasteelIngot,
		                               null, SwgItems.CraftingComponents.BallBearing, null,
		                               SwgItems.Material.DurasteelIngot, null, SwgItems.Material.DurasteelIngot)
		                      .build(assets);
	}

	public static void generateItems(List<BuiltAsset> assets)
	{
		Tarkin.registerItemFields(SwgItems.class, assets);

		PswgContent.getBlasterPresets().keySet().stream()
		           .filter(id -> id.getNamespace().equals(Resources.MODID))
		           .forEach(id -> ItemGenerator
				           .empty(Registries.ITEM.get(SwgItems.getBlasterRegistrationId(id)))
				           .lang(LanguageProvider::empty)
				           .build(assets)
		           );

		for (var entry : SwgItems.Door.DoorInsert.entrySet())
			ItemGenerator.basic(entry.getValue()).build(assets);

		ItemGenerator.armor(SwgItems.Armor.Stormtrooper, assets);
		ItemGenerator.armor(SwgItems.Armor.Shocktrooper, assets);
		ItemGenerator.armor(SwgItems.Armor.Purgetrooper, assets);
		ItemGenerator.armor(SwgItems.Armor.Artillerytrooper, assets);
		ItemGenerator.armor(SwgItems.Armor.Sandtrooper, assets);
		ItemGenerator.armor(SwgItems.Armor.Deathtrooper, assets);
		ItemGenerator.armor(SwgItems.Armor.Scouttrooper, assets);
		ItemGenerator.armor(SwgItems.Armor.Shoretrooper, assets);
		ItemGenerator.armor(SwgItems.Armor.Jumptrooper, assets);

		for (var entry : SwgItems.FoodPrep.Cups.entrySet())
			ItemGenerator.basic(entry.getValue()).build(assets);
		for (var entry : SwgItems.FoodPrep.Glasses)
			ItemGenerator.basic(entry).build(assets);
		for (var entry : SwgItems.FoodPrep.GlassBottles)
			ItemGenerator.basic(entry).build(assets);
		for (var entry : SwgItems.FoodPrep.PlasticBottles)
			ItemGenerator.basic(entry).build(assets);
	}

	public static void generateBlocks(List<BuiltAsset> assets)
	{
		Tarkin.registerBlockFields(SwgBlocks.class, assets);

		for (var block : SwgBlocks.Crate.CorrugatedCrate.values())
			BlockGenerator.blockNoModelDefaultDrops(block)
			              .blockTag(BlockTags.PICKAXE_MINEABLE)
			              .build(assets);

		for (var b : SwgBlocks.Cage.DyedCreatureTerrarium.values())
			BlockGenerator.blockNoModelDefaultDrops(b)
			              .state(BlockStateModelGenerator::createSingletonBlockState)
			              .model(ModelFile::cube_no_cull)
			              .itemModel(ModelFile::ofBlock)
			              .blockTag(BlockTags.PICKAXE_MINEABLE)
			              .build(assets);

		BlockGenerator.basic(SwgBlocks.Light.Fixture)
		              .state((block, modelId) -> BlockStateGenerator.forBooleanProperty(block, InvertedLampBlock.LIT, IdentifierUtil.concat(modelId, "_on"), modelId))
		              .models(block -> ModelFile.cubes(block, "", "_on"))
		              .itemModel(block -> ModelFile.ofBlockDifferentParent(block, IdentifierUtil.concat(AssetGenerator.getTextureName(block), "_on")))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.column(SwgBlocks.Tree.SequoiaLog, Resources.id("block/sequoia_log_top"))
		              .blockTag(SwgTags.Block.SEQUOIA_LOG)
		              .blockTag(BlockTags.AXE_MINEABLE)
		              .itemTag(SwgTags.Item.SEQUOIA_LOG)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Tree.StrippedSequoiaLog, Resources.id("block/stripped_sequoia_log_top"))
		              .blockTag(SwgTags.Block.SEQUOIA_LOG)
		              .blockTag(BlockTags.AXE_MINEABLE)
		              .itemTag(SwgTags.Item.SEQUOIA_LOG)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Tree.MossySequoiaLog, Resources.id("block/mossy_sequoia_log_top"))
		              .blockTag(SwgTags.Block.SEQUOIA_LOG)
		              .blockTag(BlockTags.AXE_MINEABLE)
		              .itemTag(SwgTags.Item.SEQUOIA_LOG)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Tree.JaporLog, Resources.id("block/japor_log_top"))
		              .blockTag(SwgTags.Block.JAPOR_LOG)
		              .blockTag(BlockTags.AXE_MINEABLE)
		              .itemTag(SwgTags.Item.JAPOR_LOG)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Tree.TatooineLog, Resources.id("block/tatooine_log_top"))
		              .blockTag(SwgTags.Block.TATOOINE_LOG)
		              .blockTag(BlockTags.AXE_MINEABLE)
		              .itemTag(SwgTags.Item.TATOOINE_LOG)
		              .build(assets);

		BlockGenerator.basic(SwgBlocks.Tree.SequoiaWood, Resources.id("block/sequoia_log"))
		              .blockTag(SwgTags.Block.SEQUOIA_LOG)
		              .blockTag(BlockTags.AXE_MINEABLE)
		              .itemTag(SwgTags.Item.SEQUOIA_LOG)
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Tree.JaporWood, Resources.id("block/japor_log"))
		              .blockTag(SwgTags.Block.JAPOR_LOG)
		              .blockTag(BlockTags.AXE_MINEABLE)
		              .itemTag(SwgTags.Item.JAPOR_LOG)
		              .build(assets);
		BlockGenerator.basic(SwgBlocks.Tree.TatooineWood, Resources.id("block/tatooine_log"))
		              .blockTag(SwgTags.Block.TATOOINE_LOG)
		              .blockTag(BlockTags.AXE_MINEABLE)
		              .itemTag(SwgTags.Item.TATOOINE_LOG)
		              .build(assets);

		BlockGenerator.basicWoodProducts(SwgBlocks.Tree.JaporProducts, BlockTags.AXE_MINEABLE, assets);
		BlockGenerator.basicWoodProducts(SwgBlocks.Tree.SequoiaProducts, BlockTags.AXE_MINEABLE, assets);

		BlockGenerator.stairs(SwgBlocks.Dirt.WetPourstoneStairs, Resources.id("block/wet_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.verticalSlab(SwgBlocks.Dirt.WetPourstoneSlab, Resources.id("block/wet_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.stairs(SwgBlocks.Dirt.RuinedWetPourstoneStairs, Resources.id("block/ruined_wet_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.verticalSlab(SwgBlocks.Dirt.RuinedWetPourstoneSlab, Resources.id("block/ruined_wet_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.MoistureVaporator.Gx8)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Power.Coupling)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		// TODO: adjust loot table to match vanilla raw ores
		BlockGenerator.basicDropFortuneBonus(SwgBlocks.Ore.BeskarOre, SwgItems.Material.BeskarRaw)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicDropFortuneBonus(SwgBlocks.Ore.ChromiumOre, SwgItems.Material.ChromiumRaw)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicDropFortuneBonus(SwgBlocks.Ore.CortosisOre, SwgItems.Material.CortosisRaw)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicDropFortuneBonus(SwgBlocks.Ore.DeshOre, SwgItems.Material.DeshRaw)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicDropFortuneBonus(SwgBlocks.Ore.DiatiumOre, SwgItems.Material.DiatiumRaw)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicDropFortuneBonus(SwgBlocks.Ore.TitaniumOre, SwgItems.Material.TitaniumRaw)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicDropFortuneBonus(SwgBlocks.Ore.IoniteOre, SwgItems.Material.IoniteRaw)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.basicDropMany(SwgBlocks.Ore.ZersiumOre, SwgItems.Material.ZersiumCrystal, 1, 3)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicDropMany(SwgBlocks.Ore.LommiteOre, SwgItems.Material.LommiteCrystal, 1, 3)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicDropMany(SwgBlocks.Ore.HeliciteOre, SwgItems.Material.HeliciteCrystal, 1, 3)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicDropMany(SwgBlocks.Ore.ThorilideOre, SwgItems.Material.ThorilideCrystal, 1, 3)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.column(SwgBlocks.Panel.ImperialCutoutPipes, Resources.id("block/imperial_cutout_pipes_face"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialCutoutCagedPipes, Resources.id("block/imperial_cutout_pipes_face"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.column(SwgBlocks.Panel.ImperialCutout, Resources.id("block/imperial_cutout_face"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.ImperialCutoutCaged, Resources.id("block/imperial_cutout_face"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.basicStoneProducts(SwgBlocks.Panel.BlackImperialPanelBlank, BlockTags.PICKAXE_MINEABLE, assets);
		BlockGenerator.basicStoneProducts(SwgBlocks.Panel.WhiteImperialPanelBlank, BlockTags.PICKAXE_MINEABLE, assets);
		BlockGenerator.basicStoneProducts(SwgBlocks.Panel.GrayImperialPanelBlank, BlockTags.PICKAXE_MINEABLE, assets);
		BlockGenerator.basicStoneProducts(SwgBlocks.Panel.LightGrayImperialPanelBlank, BlockTags.PICKAXE_MINEABLE, assets);

		for (var e : SwgBlocks.Panel.BlackImperialPanelPatternA)
			BlockGenerator.basic(e)
			              .blockTag(BlockTags.PICKAXE_MINEABLE)
			              .build(assets);
		for (var e : SwgBlocks.Panel.BlackImperialPanelPatternB)
			BlockGenerator.basic(e)
			              .blockTag(BlockTags.PICKAXE_MINEABLE)
			              .build(assets);
		for (var e : SwgBlocks.Panel.BlackImperialPanelPatternC)
			BlockGenerator.basic(e)
			              .blockTag(BlockTags.PICKAXE_MINEABLE)
			              .build(assets);
		for (var e : SwgBlocks.Panel.BlackImperialPanelPatternD)
			BlockGenerator.basic(e)
			              .blockTag(BlockTags.PICKAXE_MINEABLE)
			              .build(assets);
		for (var e : SwgBlocks.Panel.BlackImperialPanelPatternE)
			BlockGenerator.basic(e)
			              .blockTag(BlockTags.PICKAXE_MINEABLE)
			              .build(assets);

		//		BlockGenerator.basic(SwgBlocks.Panel.ImperialCutout)
		//		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		//		              .build(assets);
		//		BlockGenerator.basic(SwgBlocks.Panel.ImperialCutoutPipes)
		//		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		//		              .build(assets);

		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf1, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf2, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf3, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf4, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightHalf5, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightOff1, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightOff2, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightOn1, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialLightOn2, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.column(SwgBlocks.Panel.GrayImperialPanelPattern1, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.column(SwgBlocks.Panel.GrayImperialPanelPattern2, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.staticColumn(SwgBlocks.Panel.GrayImperialPanelPattern3, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.staticColumn(SwgBlocks.Panel.RustedGrayImperialPanelPattern3, Resources.id("block/rusted_gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.staticColumn(SwgBlocks.Panel.MossyGrayImperialPanelPattern3, Resources.id("block/mossy_gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.staticColumn(SwgBlocks.Panel.GrayImperialPanelPattern4, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.staticColumn(SwgBlocks.Panel.RustedGrayImperialPanelPattern4, Resources.id("block/rusted_gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.staticColumn(SwgBlocks.Panel.MossyGrayImperialPanelPattern4, Resources.id("block/mossy_gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.staticColumn(SwgBlocks.Panel.GrayImperialPanelPattern5, Resources.id("block/gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.staticColumn(SwgBlocks.Panel.RustedGrayImperialPanelPattern5, Resources.id("block/rusted_gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.staticColumn(SwgBlocks.Panel.MossyGrayImperialPanelPattern5, Resources.id("block/mossy_gray_imperial_panel_pattern_3"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Panel.ImperialPanelTall1)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Panel.ImperialPanelTall2)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Panel.ImperialLightTall1)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Panel.ImperialLightTall2)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Pipe.Large)
		              .itemModel(ModelFile::blockSeparateItem)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.cross(SwgBlocks.Plant.FunnelFlower).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.BlossomingFunnelFlower).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.PoontenGrass).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.DriedPoontenGrass).build(assets);
		BlockGenerator.cross(SwgBlocks.Plant.Tuber).build(assets);

		BlockGenerator.cropStages(SwgBlocks.Plant.Chasuka, SwgBlocks.Plant.Chasuka::getAgeProperty, IdentifierUtil.concat(AssetGenerator.getTextureName(SwgBlocks.Plant.Chasuka), "_stage2"))
		              .lootTable(block1 -> LootTableFile.seedCrop(block1, SwgItems.Seeds.ChasukaSeeds, SwgItems.Food.ChasukaLeaf, 2, 3, 0.5714286))
		              .build(assets);

		BlockGenerator.bushStages(SwgBlocks.Plant.HkakBush, () -> HkakBushBlock.AGE, IdentifierUtil.concat(AssetGenerator.getTextureName(SwgBlocks.Plant.HkakBush), "_stage3")).build(assets);
		BlockGenerator.bloomingBushStages(SwgBlocks.Plant.MoloShrub, () -> MoloShrubBlock.AGE, () -> MoloShrubBlock.BLOOMING, IdentifierUtil.concat(AssetGenerator.getTextureName(SwgBlocks.Plant.MoloShrub), "_stage3_blooming"))
		              .lootTable(block1 -> LootTableFile.many(block1, SwgItems.Natural.MoloFlower, new LootTableFile.Pool.CountFunction.Range(0, 2, new Identifier("uniform"))))
		              .build(assets);

		BlockGenerator.block(SwgBlocks.Plant.VaporatorMushroom)
		              .model(ModelFile::cross)
		              .itemModel(ModelFile::item)
		              .lootTable(block1 -> LootTableFile.many(block1, SwgItems.Food.VaporatorMushroom, new LootTableFile.Pool.CountFunction.Range(1, 3, new Identifier("uniform"))))
		              .build(assets);

		BlockGenerator.block(SwgBlocks.Sand.Desert)
		              .state(BlockStateModelGenerator::createBlockStateWithRandomHorizontalRotations)
		              .lootTable(LootTableFile.singleSelfWithBonus(SwgItems.Food.BlackMelon, 0.1f, 2))
		              .blockTag(SwgTags.Block.DESERT_SAND)
		              .itemTag(SwgTags.Item.DESERT_SAND)
		              .build(assets);
		BlockGenerator.block(SwgBlocks.Sand.Pit)
		              .state(BlockStateModelGenerator::createBlockStateWithRandomHorizontalRotations)
		              .lootTable(LootTableFile.singleSelfWithBonus(SwgItems.Food.BlackMelon, 0.24f, 2))
		              .blockTag(SwgTags.Block.DESERT_SAND)
		              .itemTag(SwgTags.Item.DESERT_SAND)
		              .build(assets);
		BlockGenerator.block(SwgBlocks.Sand.Fine)
		              .lootTable(LootTableFile.singleSelfWithBonus(SwgItems.Food.BlackMelon, 0.18f, 2))
		              .blockTag(SwgTags.Block.DESERT_SAND)
		              .itemTag(SwgTags.Item.DESERT_SAND)
		              .build(assets);
		BlockGenerator.accumulatingLayers(SwgBlocks.Sand.LooseDesert)
		              .blockTag(SwgTags.Block.DESERT_SAND)
		              .itemTag(SwgTags.Item.DESERT_SAND)
		              .build(assets);
		BlockGenerator.block(SwgBlocks.Sand.Canyon)
		              .state(BlockStateModelGenerator::createBlockStateWithRandomHorizontalRotations)
		              .lootTable(LootTableFile.singleSelfWithBonus(SwgItems.Food.BlackMelon, 0.04f, 2))
		              .blockTag(SwgTags.Block.DESERT_SAND)
		              .itemTag(SwgTags.Item.DESERT_SAND)
		              .build(assets);

		BlockGenerator.basicStoneProducts(SwgBlocks.Stone.Canyon, SwgTags.Block.DESERT_SANDSTONE, assets);

		BlockGenerator.stoneProducts(SwgBlocks.Sandstone.Desert, b -> BlockGenerator.staticColumn(b, Resources.id("block/smooth_desert_sandstone")), SwgTags.Block.DESERT_SANDSTONE, assets);

		BlockGenerator.staticColumnTopBottom(SwgBlocks.Sandstone.Dunestone)
		              .blockTag(SwgTags.Block.DESERT_SANDSTONE)
		              .itemTag(SwgTags.Item.DESERT_SANDSTONE)
		              .build(assets);

		BlockGenerator.basicRandomRotation(SwgBlocks.Salt.Caked)
		              .build(assets);

		BlockGenerator.basicStoneProducts(SwgBlocks.Stone.CanyonCobble, BlockTags.PICKAXE_MINEABLE, assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Stone.DurasteelConnectedPourstone)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.Massassi.block)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.verticalSlab(SwgBlocks.Stone.Massassi.slab, Resources.id("block/massassi_stone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.Massassi.stairs, Resources.id("block/massassi_stone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.wall(SwgBlocks.Stone.Massassi.wall, Resources.id("block/massassi_stone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.verticalSlabUniqueDouble(SwgBlocks.Stone.MassassiSmoothSlab, Resources.id("block/smooth_massassi_stone_slab_double"), Resources.id("block/smooth_massassi_stone"), Resources.id("block/smooth_massassi_stone_slab_side"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicStoneProducts(SwgBlocks.Stone.MassassiBricks, BlockTags.PICKAXE_MINEABLE, assets);
		//BlockGenerator.basic(SwgBlocks.Stone.MassassiChiseledBricks).build(assets);

		BlockGenerator.verticalSlabUniqueDouble(SwgBlocks.Stone.MossyMassassiSmoothSlab, Resources.id("block/mossy_smooth_massassi_stone_slab_double"), Resources.id("block/mossy_smooth_massassi_stone"), Resources.id("block/mossy_smooth_massassi_stone_slab_side"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicStoneProducts(SwgBlocks.Stone.MossyMassassiBricks, BlockTags.PICKAXE_MINEABLE, assets);

		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.Pourstone.block)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.Pourstone.stairs, Resources.id("block/pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.verticalSlab(SwgBlocks.Stone.Pourstone.slab, Resources.id("block/pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.wall(SwgBlocks.Stone.Pourstone.wall, Resources.id("block/pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.CrackedPourstone.block)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.CrackedPourstone.stairs, Resources.id("block/cracked_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.verticalSlab(SwgBlocks.Stone.CrackedPourstone.slab, Resources.id("block/cracked_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.wall(SwgBlocks.Stone.CrackedPourstone.wall, Resources.id("block/cracked_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.SmoothPourstone.block)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.SmoothPourstone.stairs, Resources.id("block/smooth_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.verticalSlab(SwgBlocks.Stone.SmoothPourstone.slab, Resources.id("block/smooth_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.wall(SwgBlocks.Stone.SmoothPourstone.wall, Resources.id("block/smooth_pourstone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		for (var pair : SwgBlocks.Stone.DyedPourstone.entrySet())
			BlockGenerator.basicStoneProducts(pair.getValue(), BlockTags.PICKAXE_MINEABLE, assets);

		BlockGenerator.basicRandomMirror(SwgBlocks.Stone.Ilum.block)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.verticalSlab(SwgBlocks.Stone.Ilum.slab, Resources.id("block/ilum_stone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.stairs(SwgBlocks.Stone.Ilum.stairs, Resources.id("block/ilum_stone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.wall(SwgBlocks.Stone.Ilum.wall, Resources.id("block/ilum_stone"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.verticalSlabUniqueDouble(SwgBlocks.Stone.IlumSmoothSlab, Resources.id("block/smooth_ilum_stone_slab_double"), Resources.id("block/smooth_ilum_stone"), Resources.id("block/smooth_ilum_stone_slab_side"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.basicStoneProducts(SwgBlocks.Stone.IlumBricks, BlockTags.PICKAXE_MINEABLE, assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Tank.FusionFuel)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Tank.StarshipFuel)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Scaffold.Scaffold)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Scaffold.ScaffoldStairs)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.staticColumn(SwgBlocks.Vent.Air, Resources.id("block/air_vent_top"), Resources.id("block/air_vent_side"))
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		BlockGenerator.trapdoor(SwgBlocks.Vent.Imperial)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.trapdoor(SwgBlocks.Vent.ImperialGrated)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);

		for (var e : SwgBlocks.Grate.ImperialOpaque)
			BlockGenerator.basic(e)
			              .blockTag(BlockTags.PICKAXE_MINEABLE)
			              .build(assets);
		for (var e : SwgBlocks.Grate.ImperialTrapdoor)
			BlockGenerator.trapdoor(e)
			              .blockTag(BlockTags.PICKAXE_MINEABLE)
			              .build(assets);

		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Workbench.Blaster)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
		BlockGenerator.blockNoModelDefaultDrops(SwgBlocks.Workbench.Lightsaber)
		              .blockTag(BlockTags.PICKAXE_MINEABLE)
		              .build(assets);
	}

	public static void build(List<BuiltAsset> assets)
	{
		generateBlocks(assets);
		generateItems(assets);
		generateRecipes(assets);
		generateTags(assets);
		generateLangEntries(assets);
	}

	public static void generateSpeciesLang(List<BuiltAsset> assets, LanguageBuilder lang, String namespace)
	{
		var speciesManager = SwgSpeciesManager.INSTANCE;
		ResourceManagerUtil.forceReload(speciesManager, ResourceType.SERVER_DATA);
		var speciesLangBase = lang.entry("species").modid();

		speciesLangBase.dot(SpeciesVariable.NONE).build(assets);

		for (var species : SwgSpeciesRegistry.ALL_SPECIES.get())
		{
			if (!species.getSlug().getNamespace().equals(namespace))
				continue;

			for (var lore : SwgSpeciesLore.values())
				lang.entry(lore.createLanguageKey(species.getSlug())).build(assets);

			speciesLangBase.dot(species.getSlug().getPath()).build(assets);

			for (var variable : species.getVariables())
			{
				lang.entry(variable.getTranslationKey()).build(assets);

				for (var value : variable.getPossibleValues())
					lang.entry(variable.getTranslationFor(value)).build(assets);
			}
		}
	}

	public static void generateBlasterLang(List<BuiltAsset> assets, LanguageBuilder lang, String namespace)
	{
		var blasterData = PswgContent.getBlasterPresets();

		for (var blasterEntry : blasterData.entrySet())
		{
			var blasterId = blasterEntry.getKey();
			if (!blasterId.getNamespace().equals(namespace))
				continue;

			var blasterDescriptor = blasterEntry.getValue();

			lang.entry(BlasterItem.getTranslationKeyForModel(blasterId)).build(assets);

			for (var attachment : blasterDescriptor.attachmentMap.values())
				lang.entry(((TranslatableTextContent)BlasterItem.getAttachmentTranslation(blasterId, attachment).getContent()).getKey()).build(assets);
		}

		for (var value : BlasterArchetype.values())
			lang.entry(value.getLangKey()).build(assets);
	}

	public static void generateConfigLang(List<BuiltAsset> assets, LanguageBuilder lang, Class<Config> config)
	{
		var autoconfig = lang.entry("text").dot("autoconfig").modid();
		autoconfig.dot("title").build(assets);
		var autoconfigOption = autoconfig.dot("option");
		Tarkin.generateLangFromConfigAnnotations(autoconfigOption, assets, config);
	}
}
