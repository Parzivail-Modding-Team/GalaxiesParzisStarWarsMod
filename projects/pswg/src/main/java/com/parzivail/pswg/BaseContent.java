package com.parzivail.pswg;

import com.parzivail.pswg.api.PswgAddon;
import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.api.PswgContent;
import com.parzivail.pswg.features.blasters.data.*;
import com.parzivail.pswg.features.lightsabers.data.LightsaberBladeType;
import com.parzivail.pswg.features.lightsabers.data.LightsaberDescriptor;
import com.parzivail.util.math.ColorUtil;
import com.parzivail.util.math.Falloff;
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
				new BlasterDescriptor(Resources.id("a280"), BlasterArchetype.RIFLE)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.BURST, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 500)
						.damage(10, 188f, Falloff.cliff(0.5))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.autoParameters(3)
						.burstParameters(2, 3, 2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 42, 12, 20, 14, 100, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f)),
				new BlasterDescriptor(Resources.id("bowcaster"), BlasterArchetype.HEAVY)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC), BlasterWaterBehavior.BOLTS_PASS_THROUGH_WATER)
						.mechanicalProperties(6.7f, -0.5f, 10, 300)
						.damage(20, 188f, Falloff.cliff(2))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1.25f, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 100, 12, 20, 14, 100, 80))
						.cooling(new BlasterCoolingBypassProfile(0.5f, 0.05f, 0.3f, 0.06f))
						.attachments(b -> b.attachment(1, "repeater", BlasterAttachmentFunction.ALLOW_BURST, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)),
				new BlasterDescriptor(Resources.id("cycler"), BlasterArchetype.SLUGTHROWER)
						.firingBehavior(List.of(BlasterFiringMode.SLUGTHROWER), BlasterWaterBehavior.CAN_FIRE_UNDERWATER)
						.mechanicalProperties(6.7f, -0.5f, 10, 100)
						.damage(16, 188f, Falloff.cliff(10))
						.bolt(0, 1, 0.75f)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 338, 100, 20, 14, 100, 40))
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
						.heat(new BlasterHeatInfo(1008, 42, 12, 20, 14, 100, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f)),
				new BlasterDescriptor(Resources.id("dc15a"), BlasterArchetype.RIFLE)
						.sound(Resources.id("dc15"))
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 400)
						.damage(10, 188f, Falloff.cliff(0.5))
						.bolt(ColorUtil.packHsv(0.62f, 1, 1), 1, 1)
						.autoParameters(4)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 42, 12, 20, 14, 100, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f)),
				new BlasterDescriptor(Resources.id("dh17"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(1, -0.5f, 10, 500)
						.damage(10, 75, Falloff.cliff(0.15))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.autoParameters(3)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 60, 10, 54, 14, 18, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.03f)),
				new BlasterDescriptor(Resources.id("dl18"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 500)
						.damage(10, 75, Falloff.cliff(0.15))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 70, 8, 20, 14, 100, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.4f, 0.04f))
						.attachments(b -> b.attachment(1, "firing", BlasterAttachmentFunction.ALLOW_AUTO, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
						                   .attachment(2, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)
						                   .attachment(3, "targeting", BlasterAttachmentFunction.REDUCE_SPREAD, BlasterAttachmentCategory.INTERNAL_TARGETING, "barrel", null)
						                   .attachment(4, "scope", BlasterAttachmentFunction.DEFAULT_SCOPE, BlasterAttachmentCategory.SCOPE, "scope", null)),
				new BlasterDescriptor(Resources.id("dl44"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.25f, 10, 500)
						.damage(10, 188, Falloff.cliff(0.1))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 120, 8, 20, 14, 100, 80))
						.cooling(new BlasterCoolingBypassProfile(0.5f, 0.05f, 0.3f, 0.02f)),
				new BlasterDescriptor(Resources.id("dlt19"), BlasterArchetype.HEAVY)
						.firingBehavior(List.of(BlasterFiringMode.AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 300)
						.damage(10, 188f, Falloff.cliff(2))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.autoParameters(2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 30, 9, 20, 14, 100, 80))
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
						.damage(6, 188f, Falloff.cliff(0.5))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.autoParameters(4)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 55, 10, 54, 14, 10, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.07f, 0.3f, 0.03f)),
				new BlasterDescriptor(Resources.id("ee3"), BlasterArchetype.RIFLE)
						.firingBehavior(List.of(BlasterFiringMode.BURST, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(2.9f, -0.5f, 10, 400)
						.damage(12, 100, Falloff.cliff(0.5))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.burstParameters(2, 3, 2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 45, 8, 54, 14, 14, 80))
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
						.mechanicalProperties(6.7f, -0.5f, 10, 100)
						.damage(10, 188f, Falloff.linear())
						.bolt(ColorUtil.packHsv(0.62f, 1, 1), 1, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 42, 8, 20, 14, 100, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.05f, 0.3f, 0.01f))
						.attachments(b -> b.attachment(1, "gas_conversion", BlasterAttachmentFunction.ION_TO_GAS_CONVERSION, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)
						                   .attachment(1, "repulsor_addon", BlasterAttachmentFunction.ION_TO_REPULSOR_CONVERSION, BlasterAttachmentCategory.INTERNAL_ORDNANCE_CONFIG)),
				new BlasterDescriptor(Resources.id("jawa_ion"), BlasterArchetype.ION)
						.firingBehavior(List.of(BlasterFiringMode.ION), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 100)
						.damage(10, 188f, Falloff.linear())
						.bolt(ColorUtil.packHsv(0.62f, 1, 1), 1, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 42, 8, 20, 14, 100, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.05f, 0.3f, 0.01f)),
				new BlasterDescriptor(Resources.id("rk3"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 500)
						.damage(10, 188, Falloff.cliff(0.15))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 85, 12, 20, 14, 100, 80))
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
						.heat(new BlasterHeatInfo(1008, 28, 8, 20, 14, 100, 80))
						.cooling(new BlasterCoolingBypassProfile(0.7f, 0.1f, 0.3f, 0.05f))
						.attachments(b -> b.attachment(1, "cooling", BlasterAttachmentFunction.IMPROVE_COOLING, BlasterAttachmentCategory.INTERNAL_COOLING)),
				new BlasterDescriptor(Resources.id("se14c"), BlasterArchetype.PISTOL)
						.firingBehavior(List.of(BlasterFiringMode.SEMI_AUTOMATIC, BlasterFiringMode.BURST, BlasterFiringMode.STUN), BlasterWaterBehavior.NONE)
						.mechanicalProperties(6.7f, -0.5f, 10, 500)
						.damage(10, 188, Falloff.cliff(0.15))
						.bolt(ColorUtil.packHsv(0.98f, 1, 1), 1, 1)
						.burstParameters(2, 5, 2)
						.recoil(new BlasterAxialInfo(1.5f, 3))
						.spread(new BlasterAxialInfo(0, 0))
						.heat(new BlasterHeatInfo(1008, 85, 20, 20, 14, 100, 80))
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
						.heat(new BlasterHeatInfo(1008, 95, 12, 20, 14, 100, 80))
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
						.bladeLength("blade_secondary_l", 0.15f)
						.bladeLength("blade_secondary_r", 0.15f),
				new LightsaberDescriptor(Resources.id("dume"), "Kanan Jarrus", ColorUtil.packHsv(0.62f, 1, 1), LightsaberBladeType.DEFAULT)
		);

		if (FabricLoader.getInstance().isDevelopmentEnvironment())
			PswgContent.registerLightsaberPreset(
					new LightsaberDescriptor(Resources.id("rig_test"), "parzi", ColorUtil.packHsv(0.62f, 1, 1), LightsaberBladeType.DEFAULT)
			);
	}

	@Override
	public void onPswgClientReady()
	{
	}
}
