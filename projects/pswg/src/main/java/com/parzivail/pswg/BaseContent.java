package com.parzivail.pswg;

import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.character.SpeciesBuilder;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.features.blasters.data.*;
import com.parzivail.pswg.features.lightsabers.data.LightsaberBladeType;
import com.parzivail.pswg.features.lightsabers.data.LightsaberDescriptor;
import com.parzivail.util.math.ColorUtil;
import com.parzivail.util.math.Falloff;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.List;

public class BaseContent implements PswgAddon
{
	@Override
	public void onPswgReady()
	{
		registerSpecies();
		registerLightsabers();
		registerBlasters();
	}

	private void registerSpecies()
	{
		var customizationOptions = new HashMap<String, List<String>>();
		customizationOptions.put("humanoid_eyebrows", List.of(
				"black",
				"blonde",
				"brown",
				"white",
				"none"
		));
		customizationOptions.put("humanoid_scars", List.of(
				"none",
				"scar_head_1",
				"scar_head_2",
				"scar_head_3",
				"scar_head_4"
		));
		customizationOptions.put("humanoid_tattoos", List.of(
				"none",
				"tattoo_head_1",
				"tattoo_head_2",
				"tattoo_head_3",
				"tattoo_head_4",
				"tattoo_head_5",
				"tattoo_head_6",
				"tattoo_head_7",
				"tattoo_head_8",
				"tattoo_leftarm_1",
				"tattoo_leftarm_2",
				"tattoo_leftarm_3",
				"tattoo_rightarm_1",
				"tattoo_rightarm_2",
				"tattoo_rightarm_3"
		));
		customizationOptions.put("humanoid_hair", List.of(
				"none",
				"1",
				"2",
				"3",
				"4",
				"5",
				"6",
				"7"
		));
		customizationOptions.put("humanoid_hair_color", List.of(
				"37281e",
				"1f1f1f",
				"c6a26e",
				"f2ce85",
				"f8e7c6",
				"edf2f1",
				"b8bcbc",
				"d34e3f",
				"e48652"
		));
		customizationOptions.put("humanoid_clothes_underlayer", List.of(
				"none",
				"body_glove",
				"jumpsuit",
				"radar_technician",
				"imperial_pilot"
		));
		customizationOptions.put("humanoid_clothes_top", List.of(
				"tatooine_civ1",
				"the_gunslinger",
				"jedi1",
				"smuggler1",
				"smuggler2",
				"smuggler3",
				"the_knight",
				"the_marshall",
				"imperial_director",
				"imperial_officer_gray",
				"imperial_officer_khaki",
				"kakashi_black",
				"kakashi_dark_blue",
				"kakashi_sleeveless_black",
				"kakashi_sleeveless_dark_blue",
				"kakashi_tan",
				"shirt_black",
				"shirt_brown",
				"vest"
		));
		customizationOptions.put("humanoid_clothes_bottom", List.of(
				"tatooine_civ1",
				"jedi1",
				"smuggler1",
				"smuggler2",
				"smuggler3",
				"the_knight",
				"the_gunslinger",
				"the_marshall",
				"imperial_director",
				"imperial_officer_gray",
				"imperial_officer_khaki",
				"cargo_brown",
				"cargo_green"
		));
		customizationOptions.put("humanoid_clothes_belt", List.of(
				"none",
				"default",
				"tatooine_civ1",
				"jedi1",
				"jedi2",
				"smuggler1",
				"smuggler2",
				"the_knight",
				"the_gunslinger",
				"the_marshall",
				"imperial_officer",
				"imperial_pilot"
		));
		customizationOptions.put("humanoid_clothes_boots", List.of(
				"none",
				"default",
				"imperial_pilot1",
				"imperial_pilot2",
				"rebel_pilot",
				"tatooine_civ1",
				"jedi1",
				"smuggler1",
				"smuggler2",
				"smuggler3",
				"the_knight",
				"the_gunslinger",
				"the_marshall",
				"imperial_officer",
				"imperial_scout_trooper"
		));
		customizationOptions.put("humanoid_clothes_gloves", List.of(
				"none",
				"imperial_pilot1",
				"imperial_pilot2",
				"rebel_pilot",
				"tatooine_civ1",
				"jedi1",
				"smuggler1",
				"smuggler2",
				"the_knight",
				"the_gunslinger",
				"the_marshall",
				"imperial_officer"
		));
		customizationOptions.put("humanoid_clothes_accessories", List.of(
				"none",
				"the_marshall",
				"goggles_black",
				"goggles_green",
				"goggles_orange",
				"mouthcover_black",
				"mouthcover_blue",
				"mouthcover_red",
				"scarf_blue",
				"scarf_brown",
				"scarf_green",
				"scarf_tan",
				"yes"
		));
		customizationOptions.put("humanoid_clothes_outerwear", List.of(
				"none",
				"tatooine_civ1",
				"jedi1",
				"smuggler1",
				"smuggler2",
				"smuggler3",
				"the_knight",
				"the_gunslinger",
				"ballistic",
				"light1",
				"radar_technician",
				"senatorial_vest",
				"heavy_blue",
				"heavy_green",
				"jedi_robe_brown",
				"jedi_robe_tan",
				"shroud_black",
				"shroud_brown",
				"shroud_tan",
				"sleeveless",
				"vest1",
				"vest2"
		));

		PswgContent.HUMANOID_CUSTOMIZATION_REGISTERED.register((id, options) -> {
			if (customizationOptions.containsKey(id))
				options.possibleValues.addAll(customizationOptions.get(id));
		});

		PswgContent.registerSpecies(
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_AQUALISH)
						.withHumanoidClothing()
						.withHumanoidBodyModifications()
						.variable("body", "beige", "green")
						.variable("eyes", "two", "four")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .then("eyes", false)
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_BITH)
						.withHumanoidClothing()
						.withHumanoidBodyModifications()
						.variable("body", "white", "green", "pink")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_CHAGRIAN)
						.withHumanoidBodyModifications()
						.withHumanoidEyes()
						.withHumanoidClothing()
						.variable("body", "blue", "black", "cyan", "gray", "light_blue")
						.variable("eyebrows", "black")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .thenHumanoidEyes()
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_CHISS)
						.withHumanoidBodyModifications()
						.withHumanoidClothing()
						.humanoidVariable("humanoid_eyebrows", "black", "white")
						.humanoidVariable("humanoid_hair", "1", "2", "3", "4")
						.variable("hair_color", 0x1f1f1f, 0xf8e7c6, 0xedf2f1, 0xb8bcbc)
						.variable("body", "1", "2", "3", "4", "5", "6")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .thenStatic("eyes")
						                         .thenGender("humanoid_eyebrows", true)
						                         .thenTint("humanoid_hair", "hair_color", true)
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_HUMAN)
						.withHumanoidBodyModifications()
						.withHumanoidEyes()
						.withHumanoidEyebrows()
						.withHumanoidClothing()
						.withHumanoidHairAndColor()
						.variable("skin_tone", "almond", "chocolate", "ivory", "limestone", "sand")
						.layerRenderer(tbb -> tbb.thenGender("skin_tone", false)
						                         .thenHumanoidBodyModifications()
						                         .thenHumanoidEyes()
						                         .thenHumanoidEyebrows()
						                         .thenHumanoidClothing()
						                         .thenHumanoidHair()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_JAWA)
						.variable("body", "brown")
						.layerRenderer(tbb -> tbb.thenGender("body", false))
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_PANTORAN)
						.withHumanoidBodyModifications()
						.withHumanoidEyes()
						.withHumanoidEyebrows()
						.withHumanoidClothing()
						.withHumanoidHair()
						.variable("tattoos", "none", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14")
						.variable("hair_color", 0xbc519c, 0xefbbff, 0xfad6ff, 0x915590, 0x37477e)
						.layerRenderer(tbb -> tbb.thenGenderStatic("skin")
						                         .thenHumanoidBodyModifications()
						                         .then("tattoos", true)
						                         .thenHumanoidEyes()
						                         .thenHumanoidEyebrows()
						                         .thenHumanoidClothing()
						                         .thenHumanoidHair("hair_color")
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_TOGRUTA)
						.withHumanoidBodyModifications()
						.withHumanoidEyes()
						.withHumanoidClothing()
						.variable("body", "orange", "blue", "gray", "green", "light_blue", "light_gray", "lilac", "pink", "red", "teal", "yellow", "light_yellow")
						.variable("face", "none", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22")
						.variable("eyebrows", "default")
						.variable("lower_montral", "cream", "beige", "white")
						.variable("upper_montral", "blue", "none", "brown", "dark_blue", "green", "light_green", "pink", "purple", "red", "turquoise")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .thenGender("lower_montral", true)
						                         .thenGender("upper_montral", true)
						                         .then("face", true)
						                         .thenHumanoidEyes()
						                         .thenGender("eyebrows", true)
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_TWILEK)
						.withHumanoidBodyModifications()
						.withHumanoidEyes()
						.withHumanoidEyebrows()
						.withHumanoidClothing()
						.variable("body", "green", "blue", "cyan", "flesh", "lime", "magenta", "orange", "pink", "purple", "red", "white", "yellow")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .thenHumanoidEyes()
						                         .thenHumanoidEyebrows()
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_WOOKIEE)
						.variable("body", "chestnut", "black", "brown", "gray")
						.layerRenderer(tbb -> tbb.then("body", false)
						                         .thenStatic("eyes")
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_DEVARONIAN)
						.withHumanoidBodyModifications()
						.withHumanoidEyes()
						.withHumanoidEyebrows()
						.withHumanoidClothing()
						.withHumanoidHairAndColor()
						.variable("body", "red", "chartreuse", "darkred", "flesh", "green", "mustard", "orange", "pink", "red")
						.variable("horns", "1", "2", "3", "4", "5", "6")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .thenHumanoidEyes()
						                         .thenHumanoidEyebrows()
						                         .thenHumanoidClothing()
						                         .thenHumanoidHair()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_RODIAN)
						.withHumanoidBodyModifications()
						.withHumanoidClothing()
						.variable("body", "green", "blue", "brown", "cyan", "dark_green", "flesh", "orange", "purple", "red", "violet")
						.variable("eyes", "violet", "black", "blue", "green", "purple")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .then("eyes", false)
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_DUROS)
						.withHumanoidBodyModifications()
						.withHumanoidClothing()
						.variable("body", "light_blue", "blue", "cyan", "green", "gunmetal", "purple", "turquoise")
						.variable("eyes", "small", "large")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .then("eyes", false)
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_GOTAL)
						.withHumanoidEyes()
						.withHumanoidClothing()
						.variable("fur", "cedar", "blonde", "brown", "chocolate", "maroon", "peanut", "red", "white")
						.variable("skin", "gray", "ash", "cream", "olive", "peach", "pink", "slate", "tan")
						.variable("horns", "1", "2", "3", "4", "5", "6")
						.variable("beard", "1", "2", "3", "4", "5", "6", "7")
						.variable("eye_color", 0xF3C150)
						.layerRenderer(tbb -> tbb.thenGender("fur", false)
						                         .then("skin", false)
						                         .thenTintStatic("eyes_whites", "eye_color")
						                         .thenStatic("eyes")
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_GRAN)
						.withHumanoidBodyModifications()
						.withHumanoidClothing()
						.variable("body", "orange", "banana", "bronze", "coral", "cream", "tangerine", "yellow")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .thenHumanoidBodyModifications()
						                         .thenHumanoidClothing()
						)
						.build(),
				new SpeciesBuilder(SwgSpeciesRegistry.SPECIES_ONGREE)
						.withHumanoidClothing()
						.variable("body", "orange", "brass", "brown", "gold", "green", "silver", "tangerine", "yellow")
						.variable("eyes", "yellow", "orange", "pink", "white")
						.variable("mouth", "gray", "pink", "white")
						.layerRenderer(tbb -> tbb.thenGender("body", false)
						                         .then("eyes", false)
						                         .then("mouth", false)
						                         .thenHumanoidClothing()
						)
						.build()
		);
	}

	private static void registerBlasters()
	{
		PswgContent.registerBlasterPreset(
				new BlasterDescriptor(Resources.id("a280"), BlasterArchetype.RIFLE)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.BURST, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 500)
						.damage(10, 188f, Falloff.cliff(5))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.autoParameters(3)
						.burstParameters(2, 3, 2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 42, 24, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f)),
				new BlasterDescriptor(Resources.id("bowcaster"), BlasterArchetype.HEAVY)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC), BlasterWaterBehavior.BOLTS_PASS_THROUGH_WATER)
						.mechanicalProperties(6.7f, -0.5f, 10, 300)
						.damage(20, 188f, Falloff.cliff(2))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1.25f, 1)
						.recoil(new BlasterAxialInfo(2, 5))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 100, 24, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.5f, 0.05f, 0.3f, 0.06f))
						.attachments(b -> b.attachment(1, "repeater", BlasterAttachmentFunction.ALLOW_BURST, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)),
				new BlasterDescriptor(Resources.id("cycler"), BlasterArchetype.SLUGTHROWER)
						.firingBehavior(List.of(BlasterFiringMode.SLUGTHROWER), BlasterWaterBehavior.CAN_FIRE_UNDERWATER)
						.mechanicalProperties(6.7f, -0.5f, 10, 100)
						.damage(16, 188f, Falloff.cliff(10))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 0.75f)
						.recoil(new BlasterAxialInfo(3, 8))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 338, 200, 20, 14, 20, 40))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.08f, 0.3f, 0.03f))
						.attachments(b -> b.attachment(1, "barrel", BlasterAttachmentFunction.REDUCE_RECOIL, BlasterAttachmentCategory.BARREL, "barrel_extension", null)
						                   .attachment(2, "scope", BlasterAttachmentFunction.SNIPER_SCOPE, BlasterAttachmentCategory.SCOPE, "scope_extension", null)),
				new BlasterDescriptor(Resources.id("dc15"), BlasterArchetype.HEAVY)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 300)
						.damage(10, 188f, Falloff.cliff(2))
						.bolt(ColorUtil.packHsv(0.62f, 1, 1), 1, 1)
						.autoParameters(3)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 42, 24, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f)),
				new BlasterDescriptor(Resources.id("dc15a"), BlasterArchetype.RIFLE)
						.sound(Resources.id("dc15"))
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 400)
						.damage(10, 188f, Falloff.cliff(5))
						.bolt(ColorUtil.packHsv(0.62f, 1, 1), 1, 1)
						.autoParameters(4)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 42, 24, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f)),
				new BlasterDescriptor(Resources.id("dh17"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(1, -0.5f, 10, 500)
						.damage(10, 75, Falloff.cliff(1))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.autoParameters(3)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 60, 20, 54, 14, 5, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.03f)),
				new BlasterDescriptor(Resources.id("dl18"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 500)
						.damage(10, 75, Falloff.cliff(1))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 70, 16, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.4f, 0.04f))
						.attachments(b -> b.attachment(1, "firing", BlasterAttachmentFunction.ALLOW_AUTO, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
						                   .attachment(2, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)
						                   .attachment(3, "targeting", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.INTERNAL_TARGETING, "barrel", null)
						                   .attachment(4, "scope", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "scope", null)),
				new BlasterDescriptor(Resources.id("dl44"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.25f, 10, 500)
						.damage(10, 188, Falloff.cliff(1))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 120, 16, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.5f, 0.05f, 0.3f, 0.02f)),
				new BlasterDescriptor(Resources.id("dlt19"), BlasterArchetype.HEAVY)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 300)
						.damage(10, 188f, Falloff.cliff(2))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.autoParameters(2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 30, 18, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.8f, 0.06f, 0.3f, 0.03f))
						.attachments(b -> b.attachment(1, "scope_d", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "scope_d", null)
						                   .attachment(1, "scope_x", BlasterAttachmentFunction.SNIPER_SCOPE, BlasterAttachmentCategory.SCOPE, "scope_x", null)
						                   .attachment(2, "bipod", BlasterAttachmentFunction.REDUCE_RECOIL, BlasterAttachmentCategory.BARREL, "bipod", null)
						                   .attachment(2, "barrel_d", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.BARREL, "barrel_d", null)
						                   .attachment(3, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)
						                   .attachment(4, "rapidfire", BlasterAttachmentFunction.INCREASE_RATE, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
						                   .preset(2, "bipod")),
				new BlasterDescriptor(Resources.id("e11"), BlasterArchetype.RIFLE)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(2.7f, -0.5f, 10, 400)
						.damage(6, 188f, Falloff.cliff(5))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.autoParameters(4)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 55, 20, 54, 14, 5, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.07f, 0.3f, 0.03f)),
				new BlasterDescriptor(Resources.id("ee3"), BlasterArchetype.RIFLE)
						.firingBehavior(List.of(BlasterFiringMode.BURST, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(2.9f, -0.5f, 10, 400)
						.damage(12, 100, Falloff.cliff(5))
						.bolt(ColorUtil.packHsv(0.98f, 0.51f, 1), 1, 1)
						.burstParameters(2, 3, 2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 45, 16, 54, 14, 5, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.07f, 0.2f, 0.03f))
						.attachments(b -> b.attachment(1, "stock_wood", BlasterAttachmentFunction.REDUCE_RECOIL, BlasterAttachmentCategory.STOCK, "stock", Resources.id("textures/item/model/blaster/ee3_esb.png"))
						                   .attachment(1, "stock_metal", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.STOCK, "stock", Resources.id("textures/item/model/blaster/ee3_rotj.png"))
						                   .attachment(2, "tall_scope", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "tall_scope", Resources.id("textures/item/model/blaster/ee3_esb.png"))
						                   .attachment(2, "short_scope", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "short_scope", Resources.id("textures/item/model/blaster/ee3_rotj.png"))
						                   .attachment(3, "smooth_barrel", BlasterAttachmentFunction.INCREASE_RANGE, BlasterAttachmentCategory.BARREL, "smooth_barrel", Resources.id("textures/item/model/blaster/ee3_esb.png"))
						                   .attachment(3, "vane_barrel", BlasterAttachmentFunction.ALLOW_BURST, BlasterAttachmentCategory.BARREL, "vane_barrel", Resources.id("textures/item/model/blaster/ee3_rotj.png"))
						                   .requireLayer(1, "stock_wood")
						                   .requireLayer(2, "tall_scope")
						                   .requireLayer(3, "smooth_barrel")),
				new BlasterDescriptor(Resources.id("ca87"), BlasterArchetype.ION)
						.firingBehavior(List.of(BlasterFiringMode.ION), BlasterWaterBehavior.NONE)
						.usePyrotechnics()
						.mechanicalProperties(6.7f, -0.5f, 10, 100)
						.damage(10, 20, Falloff.linear())
						.bolt(ColorUtil.packHsv(0.62f, 0.51f, 1), 0.5f, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 42, 16, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.05f, 0.3f, 0.01f))
						.attachments(b -> b.attachment(1, "gas_conversion", BlasterAttachmentFunction.ION_TO_GAS_CONVERSION, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
						                   .attachment(1, "repulsor_addon", BlasterAttachmentFunction.ION_TO_REPULSOR_CONVERSION, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)),
				new BlasterDescriptor(Resources.id("jawa_ion"), BlasterArchetype.ION)
						.firingBehavior(List.of(BlasterFiringMode.ION), BlasterWaterBehavior.NONE)
						.usePyrotechnics()
						.mechanicalProperties(6.7f, -0.5f, 10, 100)
						.damage(10, 20, Falloff.linear())
						.bolt(ColorUtil.packHsv(0.62f, 1, 1), 0.5f, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 42, 16, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.05f, 0.3f, 0.01f)),
				new BlasterDescriptor(Resources.id("rk3"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 500)
						.damage(10, 188, Falloff.cliff(1))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 85, 24, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.02f))
						.attachments(b -> b.attachment(1, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)),
				new BlasterDescriptor(Resources.id("rt97c"), BlasterArchetype.HEAVY)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 300)
						.damage(10, 188f, Falloff.cliff(2))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1.25f)
						.autoParameters(2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 28, 16, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f))
						.attachments(b -> b.attachment(1, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)),
				new BlasterDescriptor(Resources.id("se14c"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.BURST, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 500)
						.damage(10, 188, Falloff.cliff(1))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.burstParameters(2, 5, 2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 85, 40, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.03f))
						.attachments(b -> b.attachment(1, "barrel_extension", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.BARREL, "barrel_extension", null)
						                   .attachment(2, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)),
				new BlasterDescriptor(Resources.id("t21"), BlasterArchetype.HEAVY)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.BURST, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 300)
						.damage(10, 188f, Falloff.cliff(2))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.autoParameters(7)
						.burstParameters(2, 3, 2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 95, 24, 20, 14, 20, 80))
						.cooling(new BlasterCoolingBypassProfile(0.5f, 0.05f, 0.3f, 0.03f))
						.attachments(b -> b.attachment(1, "long_barrel", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.BARREL, "long_barrel", null)
						                   .attachment(1, "short_barrel", BlasterAttachmentFunction.INCREASE_DAMAGE, BlasterAttachmentCategory.BARREL, "short_barrel", null)
						                   .attachment(2, "scope", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "scope", null)
						                   .attachment(3, "repeater", BlasterAttachmentFunction.ALLOW_AUTO, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
						                   .requireLayer(1, "long_barrel"))
		);
	}

	private static void registerLightsabers()
	{
		PswgContent.registerLightsaberPreset(
				new LightsaberDescriptor(Resources.id("anakin"), "Anakin Skywalker", ColorUtil.packHsv(0.62f, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("ezra_padawan"), "Ezra Bridger", ColorUtil.packHsv(0.33f, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("kenobi"), "Obi-Wan Kenobi", ColorUtil.packHsv(0.62f, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("luke_rotj"), "Luke Skywalker", ColorUtil.packHsv(0.33f, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("qui_gon"), "Qui-Gon Jinn", ColorUtil.packHsv(0.33f, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("bob"), "Jedi Bob", ColorUtil.packHsv(0.33f, 1, 1), LightsaberBladeType.BRICK),
				new LightsaberDescriptor(Resources.id("kestis"), "Cal Kestis", ColorUtil.packHsv(0.73f, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("maul_half"), "Darth Maul", ColorUtil.packHsv(0, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("maul"), "Darth Maul", ColorUtil.packHsv(0, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("darksaber"), null, ColorUtil.packHsv(0, 0, 1), LightsaberBladeType.DARKSABER),
				new LightsaberDescriptor(Resources.id("vader"), "Darth Vader", ColorUtil.packHsv(0, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("dooku"), "Count Dooku", ColorUtil.packHsv(0, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("revan"), "Darth Revan", ColorUtil.packHsv(0.81f, 1, 1), LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("kylo"), "Kylo Ren", ColorUtil.packHsv(0, 1, 1), LightsaberBladeType.DEFAULT)
						.unstable()
						.bladeLength("blade_secondary_l", 0.15f)
						.bladeLength("blade_secondary_r", 0.15f),
				new LightsaberDescriptor(Resources.id("dume"), "Kanan Jarrus", ColorUtil.packHsv(0.62f, 1, 1), LightsaberBladeType.DEFAULT)
		);

		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			PswgContent.registerLightsaberPreset(
					new LightsaberDescriptor(Resources.id("rig_test"), "parzi", ColorUtil.packHsv(0.62f, 1, 1), LightsaberBladeType.DEFAULT)
			);
	}
}
