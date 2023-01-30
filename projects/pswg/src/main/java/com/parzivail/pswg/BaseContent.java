package com.parzivail.pswg;

import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.item.blaster.data.*;
import com.parzivail.pswg.item.lightsaber.data.LightsaberBladeType;
import com.parzivail.pswg.item.lightsaber.data.LightsaberDescriptor;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

public class BaseContent implements PswgAddon, PswgClientAddon
{
	@Override
	public void onPswgReady()
	{
		registerLightsabers();
		registerBlasters();
	}

	private static void registerBlasters()
	{
		PswgContent.registerBlasterPreset(
				new BlasterDescriptor(
						Resources.id("a280"),
						Resources.id("a280"),
						BlasterArchetype.RIFLE,
						List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.BURST, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 188f, 6.7f, 0.98f, 500,
						3, 2, 3, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 42, 12, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f),
						new BlasterAttachmentBuilder().build()
				),
				new BlasterDescriptor(
						Resources.id("bowcaster"),
						Resources.id("bowcaster"),
						BlasterArchetype.HEAVY,
						List.of(BlasterFiringMode.SEMI_AUTOMATIC),
						BlasterWaterBehavior.BOLTS_PASS_THROUGH_WATER,
						3.06f, 188f, 6.7f, 0.98f, 300,
						7, 2, 3, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 100, 12, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.5f, 0.05f, 0.3f, 0.06f),
						new BlasterAttachmentBuilder()
								.attachment(1, "repeater", BlasterAttachmentFunction.ALLOW_BURST, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
								.build()
				),
				new BlasterDescriptor(
						Resources.id("cycler"),
						Resources.id("cycler"),
						BlasterArchetype.SLUGTHROWER,
						List.of(BlasterFiringMode.SLUGTHROWER),
						BlasterWaterBehavior.CAN_FIRE_UNDERWATER,
						8, 188f, 6.7f, 0, 100,
						6, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 338, 100, 20, 14, 100, 40),
						new BlasterCoolingBypassProfile(0.7f, 0.08f, 0.3f, 0.03f),
						new BlasterAttachmentBuilder()
								.attachment(1, "barrel", BlasterAttachmentFunction.REDUCE_RECOIL, BlasterAttachmentCategory.BARREL, "barrel_extension", null)
								.attachment(2, "scope", BlasterAttachmentFunction.SNIPER_SCOPE, BlasterAttachmentCategory.SCOPE, "scope_extension", null)
								.build()
				),
				new BlasterDescriptor(
						Resources.id("dc15"),
						Resources.id("dc15"),
						BlasterArchetype.HEAVY,
						List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 188f, 6.7f, 0.62f, 300,
						3, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 42, 12, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f),
						new BlasterAttachmentBuilder().build()
				),
				new BlasterDescriptor(
						Resources.id("dc15a"),
						Resources.id("dc15"),
						BlasterArchetype.RIFLE,
						List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 188f, 6.7f, 0.62f, 400,
						4, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 42, 12, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f),
						new BlasterAttachmentBuilder().build()
				),
				new BlasterDescriptor(
						Resources.id("dh17"),
						Resources.id("dh17"),
						BlasterArchetype.PISTOL,
						List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 75, 1, 0.98f, 500,
						3, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 60, 10, 54, 14, 18, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.03f),
						new BlasterAttachmentBuilder().build()
				),
				new BlasterDescriptor(
						Resources.id("dl18"),
						Resources.id("dl18"),
						BlasterArchetype.PISTOL,
						List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 75, 6.7f, 0.98f, 500,
						4, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 70, 8, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.4f, 0.04f),
						new BlasterAttachmentBuilder()
								.attachment(1, "firing", BlasterAttachmentFunction.ALLOW_AUTO, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
								.attachment(2, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)
								.attachment(3, "targeting", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.INTERNAL_TARGETING, "barrel", null)
								.attachment(4, "scope", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "scope", null)
								.build()
				),
				new BlasterDescriptor(
						Resources.id("dl44"),
						Resources.id("dl44"),
						BlasterArchetype.PISTOL,
						List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 188, 6.7f, 0.98f, 500,
						5, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 120, 8, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.5f, 0.05f, 0.3f, 0.02f),
						new BlasterAttachmentBuilder().build()
				),
				new BlasterDescriptor(
						Resources.id("dlt19"),
						Resources.id("dlt19"),
						BlasterArchetype.HEAVY,
						List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 188f, 6.7f, 0.98f, 300,
						2, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 30, 9, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.8f, 0.06f, 0.3f, 0.03f),
						new BlasterAttachmentBuilder()
								.attachment(1, "scope_d", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "scope_d", null)
								.attachment(1, "scope_x", BlasterAttachmentFunction.SNIPER_SCOPE, BlasterAttachmentCategory.SCOPE, "scope_x", null)
								.attachment(2, "bipod", BlasterAttachmentFunction.REDUCE_RECOIL, BlasterAttachmentCategory.BARREL, "bipod", null)
								.attachment(2, "barrel_d", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.BARREL, "barrel_d", null)
								.attachment(3, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)
								.attachment(4, "rapidfire", BlasterAttachmentFunction.INCREASE_RATE, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
								.preset(2, "bipod")
								.build()
				),
				new BlasterDescriptor(
						Resources.id("e11"),
						Resources.id("e11"),
						BlasterArchetype.RIFLE,
						List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						1.75f, 188f, 2.7f, 0.98f, 400,
						4, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 55, 10, 54, 14, 10, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.07f, 0.3f, 0.03f),
						new BlasterAttachmentBuilder().build()
				),
				new BlasterDescriptor(
						Resources.id("ee3"),
						Resources.id("ee3"),
						BlasterArchetype.RIFLE,
						List.of(BlasterFiringMode.BURST, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.5f, 100, 2.9f, 0.98f, 400,
						0, 2, 3, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 45, 8, 54, 14, 14, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.07f, 0.2f, 0.03f),
						new BlasterAttachmentBuilder()
								.attachment(1, "stock_wood", BlasterAttachmentFunction.REDUCE_RECOIL, BlasterAttachmentCategory.STOCK, "stock", Resources.id("textures/item/model/blaster/ee3_esb.png"))
								.attachment(1, "stock_metal", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.STOCK, "stock", Resources.id("textures/item/model/blaster/ee3_rotj.png"))
								.attachment(2, "tall_scope", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "tall_scope", Resources.id("textures/item/model/blaster/ee3_esb.png"))
								.attachment(2, "short_scope", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "short_scope", Resources.id("textures/item/model/blaster/ee3_rotj.png"))
								.attachment(3, "smooth_barrel", BlasterAttachmentFunction.INCREASE_RANGE, BlasterAttachmentCategory.BARREL, "smooth_barrel", Resources.id("textures/item/model/blaster/ee3_esb.png"))
								.attachment(3, "vane_barrel", BlasterAttachmentFunction.ALLOW_BURST, BlasterAttachmentCategory.BARREL, "vane_barrel", Resources.id("textures/item/model/blaster/ee3_rotj.png"))
								.required(1)
								.preset(1, "stock_wood")
								.required(2)
								.preset(2, "tall_scope")
								.required(3)
								.preset(3, "smooth_barrel")
								.build()
				),
				new BlasterDescriptor(
						Resources.id("ca87"),
						Resources.id("ion"),
						BlasterArchetype.ION,
						List.of(BlasterFiringMode.ION),
						BlasterWaterBehavior.NONE,
						3.06f, 188f, 6.7f, 0.62f, 100,
						11, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 42, 8, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.05f, 0.3f, 0.01f),
						new BlasterAttachmentBuilder()
								.attachment(1, "gas_conversion", BlasterAttachmentFunction.ION_TO_GAS_CONVERSION, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
								.attachment(1, "repulsor_addon", BlasterAttachmentFunction.ION_TO_REPULSOR_CONVERSION, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
								.build()
				),
				new BlasterDescriptor(
						Resources.id("jawa_ion"),
						Resources.id("ion"),
						BlasterArchetype.ION,
						List.of(BlasterFiringMode.ION),
						BlasterWaterBehavior.NONE,
						3.06f, 188f, 6.7f, 0.62f, 100,
						11, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 42, 8, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.05f, 0.3f, 0.01f),
						new BlasterAttachmentBuilder()
								.build()
				),
				new BlasterDescriptor(
						Resources.id("rk3"),
						Resources.id("rk3"),
						BlasterArchetype.PISTOL,
						List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 188, 6.7f, 0.98f, 500,
						4, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 85, 12, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.02f),
						new BlasterAttachmentBuilder()
								.attachment(1, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)
								.build()
				),
				new BlasterDescriptor(
						Resources.id("rt97c"),
						Resources.id("rt97c"),
						BlasterArchetype.HEAVY,
						List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 188f, 6.7f, 0.98f, 300,
						2, 0, 0, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 28, 8, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f),
						new BlasterAttachmentBuilder()
								.attachment(1, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)
								.build()
				),
				new BlasterDescriptor(
						Resources.id("se14c"),
						Resources.id("se14c"),
						BlasterArchetype.PISTOL,
						List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.BURST, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 188, 6.7f, 0.98f, 500,
						4, 2, 5, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 85, 20, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.03f),
						new BlasterAttachmentBuilder()
								.attachment(1, "barrel_extension", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.BARREL, "barrel_extension", null)
								.attachment(2, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)
								.build()
				),
				new BlasterDescriptor(
						Resources.id("t21"),
						Resources.id("t21"),
						BlasterArchetype.HEAVY,
						List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.BURST, BlasterFiringMode.STUN),
						BlasterWaterBehavior.NONE,
						3.06f, 188f, 6.7f, 0.98f, 300,
						7, 2, 3, 10,
						new BlasterAxialInfo(1.5f, 3),
						new BlasterAxialInfo(0, 0),
						new BlasterHeatInfo(1008, 95, 12, 20, 14, 100, 80),
						new BlasterCoolingBypassProfile(0.5f, 0.05f, 0.3f, 0.03f),
						new BlasterAttachmentBuilder()
								.attachment(1, "long_barrel", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.BARREL, "long_barrel", null)
								.attachment(1, "short_barrel", BlasterAttachmentFunction.INCREASE_DAMAGE, BlasterAttachmentCategory.BARREL, "short_barrel", null)
								.attachment(2, "scope", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "scope", null)
								.attachment(3, "repeater", BlasterAttachmentFunction.ALLOW_AUTO, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
								.required(1)
								.preset(1, "long_barrel")
								.build()
				)
		);
	}

	private static void registerLightsabers()
	{
		PswgContent.registerLightsaberPreset(
				new LightsaberDescriptor(Resources.id("anakin"), "Anakin Skywalker", 0.62f, 1, 1, LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("ezra_padawan"), "Ezra Bridger", 0.33f, 1, 1, LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("kenobi"), "Obi-Wan Kenobi", 0.62f, 1, 1, LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("luke_rotj"), "Luke Skywalker", 0.33f, 1, 1, LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("qui_gon"), "Qui-Gon Jinn", 0.33f, 1, 1, LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("bob"), "Jedi Bob", 0.33f, 1, 1, LightsaberBladeType.BRICK),
				new LightsaberDescriptor(Resources.id("kestis"), "Cal Kestis", 0.73f, 1, 1, LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("maul_half"), "Darth Maul", 0, 1, 1, LightsaberBladeType.DEFAULT),
				new LightsaberDescriptor(Resources.id("darksaber"), null, 0, 0, 1, LightsaberBladeType.DARKSABER),
				new LightsaberDescriptor(Resources.id("vader"), "Darth Vader", 0, 1, 1, LightsaberBladeType.DEFAULT)
		);

		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			PswgContent.registerLightsaberPreset(
					new LightsaberDescriptor(Resources.id("rig_test"), "parzi", 0.62f, 1, 1, LightsaberBladeType.DEFAULT)
			);
	}

	@Override
	public void onPswgClientReady()
	{
	}
}
