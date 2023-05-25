package com.parzivail.pswg;

import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.client.render.player.PlayerSpeciesModelRenderer;
import com.parzivail.pswg.client.species.SwgSpeciesIcons;
import com.parzivail.pswg.client.species.SwgSpeciesRenderer;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.math.MathHelper;

public class BaseClientContent implements PswgClientAddon
{
	@Override
	public void onPswgClientReady()
	{
		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_AQUALISH, Resources.id("textures/gui/character/icons.png"), 8);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_AQUALISH, SwgSpeciesRenderer.humanoidBase(Resources.id("species/aqualish")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_BITH, Resources.id("textures/gui/character/icons.png"), 12);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_BITH, SwgSpeciesRenderer.humanoidBase(Resources.id("species/bith")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_CHAGRIAN, Resources.id("textures/gui/character/icons.png"), 9);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_CHAGRIAN, SwgSpeciesRenderer.humanoidBase(Resources.id("species/chagrian")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_CHISS, Resources.id("textures/gui/character/icons.png"), 3);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_CHISS, SwgSpeciesRenderer.humanoidBase(Resources.id("species/human")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_KAMINOAN, Resources.id("textures/gui/character/icons.png"), 15);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_KAMINOAN, SwgSpeciesRenderer.fullModel(Resources.id("species/kaminoan")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_JAWA, Resources.id("textures/gui/character/icons.png"), 1);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_JAWA, SwgSpeciesRenderer.humanoidBase(Resources.id("species/jawa")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_TOGRUTA, Resources.id("textures/gui/character/icons.png"), 10);
		SwgSpeciesRenderer.register(
				SwgSpeciesRegistry.SPECIES_TOGRUTA,
				SwgSpeciesRenderer.humanoidBase(Resources.id("species/togruta_m")),
				SwgSpeciesRenderer.humanoidBase(Resources.id("species/togruta_f")),
				null
		);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_TWILEK, Resources.id("textures/gui/character/icons.png"), 6);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_TWILEK, SwgSpeciesRenderer.humanoidBase(Resources.id("species/twilek")), BaseClientContent::animateTwilek);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_HUMAN, Resources.id("textures/gui/character/icons.png"), 2);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_HUMAN, SwgSpeciesRenderer.humanoidBase(Resources.id("species/human")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_PANTORAN, Resources.id("textures/gui/character/icons.png"), 4);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_PANTORAN, SwgSpeciesRenderer.humanoidBase(Resources.id("species/human")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_WOOKIEE, Resources.id("textures/gui/character/icons.png"), 13);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_WOOKIEE, SwgSpeciesRenderer.fullModel(Resources.id("species/wookiee")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_DEVARONIAN, Resources.id("textures/gui/character/icons.png"), 5);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_DEVARONIAN, SwgSpeciesRenderer.humanoidBase(Resources.id("species/devaronian")), BaseClientContent::animateDevaronian);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_RODIAN, Resources.id("textures/gui/character/icons.png"), 11);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_RODIAN, SwgSpeciesRenderer.humanoidBase(Resources.id("species/rodian")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_DUROS, Resources.id("textures/gui/character/icons.png"), 7);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_DUROS, SwgSpeciesRenderer.humanoidBase(Resources.id("species/duros")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_GOTAL, Resources.id("textures/gui/character/icons.png"), 18);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_GOTAL, SwgSpeciesRenderer.humanoidBase(Resources.id("species/gotal")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_TRANDOSHAN, Resources.id("textures/gui/character/icons.png"), 14);
		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_MON_CALAMARI, Resources.id("textures/gui/character/icons.png"), 16);
		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_IKTOTCHI, Resources.id("textures/gui/character/icons.png"), 17);
	}

	public static void animateTwilek(SwgSpecies species, AbstractClientPlayerEntity entity, PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerSpeciesModelRenderer renderer, float tickDelta)
	{
		if (!model.head.hasChild("TailBaseL"))
			return;

		var h = entity.getPitch(tickDelta) * MathHelper.RADIANS_PER_DEGREE;
		var h2 = (float)Math.pow(h, 2);
		var h3 = (float)Math.pow(h, 3);
		var h4 = (float)Math.pow(h, 4);

		var tailBaseL = model.head.getChild("TailBaseL");
		var tailMidL = tailBaseL.getChild("TailMidL");
		var tailLowerL = tailMidL.getChild("TailLowerL");

		var tailBaseR = model.head.getChild("TailBaseR");
		var tailMidR = tailBaseR.getChild("TailMidR");
		var tailLowerR = tailMidR.getChild("TailLowerR");

		// https://www.desmos.com/calculator/52kcd69qgc
		tailBaseL.pitch = tailBaseR.pitch = (-2.34f * h3 + 11.05f * h2 + 3.4f * h + 7.06f) * MathHelper.RADIANS_PER_DEGREE;
		tailMidL.pitch = tailMidR.pitch = (5.11f * h4 + 2.01f * h3 - 10.2f * h2 - 26.38f * h - 1.48f) * MathHelper.RADIANS_PER_DEGREE;
		tailLowerL.pitch = tailLowerR.pitch = (-3.15f * h4 + 2.57f * h2 - 23.03f * h - 2.93f) * MathHelper.RADIANS_PER_DEGREE;

		var y = MathHelper.wrapDegrees(model.head.yaw * MathHelper.DEGREES_PER_RADIAN) * MathHelper.RADIANS_PER_DEGREE;
		tailBaseL.roll = Math.max(0, y / 3f) + Math.min(0, y / 9f);
		tailBaseR.roll = Math.min(0, y / 3f) + Math.max(0, y / 9f);
	}

	public static void animateDevaronian(SwgSpecies species, AbstractClientPlayerEntity entity, PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerSpeciesModelRenderer renderer, float tickDelta)
	{
		if (!model.head.hasChild("horns1"))
			return;

		var hornsVariable = species.getVariableReference("horns");
		if (hornsVariable == null)
			return;

		var horns = species.getVariable(hornsVariable);

		for (var i = 0; i < 6; i++)
		{
			var hornId = String.valueOf(i + 1);
			model.head.getChild("horns%s".formatted(hornId)).visible = horns.equals(hornId);
		}
	}
}
