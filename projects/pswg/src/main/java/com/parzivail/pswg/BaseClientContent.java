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
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_AQUALISH, SwgSpeciesRenderer.model(Resources.id("species/aqualish")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_BITH, Resources.id("textures/gui/character/icons.png"), 12);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_BITH, SwgSpeciesRenderer.model(Resources.id("species/bith")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_CHAGRIAN, Resources.id("textures/gui/character/icons.png"), 9);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_CHAGRIAN, SwgSpeciesRenderer.model(Resources.id("species/chagrian")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_CHISS, Resources.id("textures/gui/character/icons.png"), 3);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_CHISS, SwgSpeciesRenderer.model(Resources.id("species/human")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_KAMINOAN, Resources.id("textures/gui/character/icons.png"), 15);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_KAMINOAN, SwgSpeciesRenderer.model(Resources.id("species/kaminoan")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_JAWA, Resources.id("textures/gui/character/icons.png"), 1);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_JAWA, SwgSpeciesRenderer.model(Resources.id("species/jawa")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_TOGRUTA, Resources.id("textures/gui/character/icons.png"), 10);
		SwgSpeciesRenderer.register(
				SwgSpeciesRegistry.SPECIES_TOGRUTA,
				SwgSpeciesRenderer.model(Resources.id("species/togruta_m")),
				SwgSpeciesRenderer.model(Resources.id("species/togruta_f")),
				null
		);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_TWILEK, Resources.id("textures/gui/character/icons.png"), 6);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_TWILEK, SwgSpeciesRenderer.model(Resources.id("species/twilek")), BaseClientContent::animateTwilek);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_HUMAN, Resources.id("textures/gui/character/icons.png"), 2);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_HUMAN, SwgSpeciesRenderer.model(Resources.id("species/human")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_PANTORAN, Resources.id("textures/gui/character/icons.png"), 4);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_PANTORAN, SwgSpeciesRenderer.model(Resources.id("species/human")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_WOOKIEE, Resources.id("textures/gui/character/icons.png"), 13);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_WOOKIEE, SwgSpeciesRenderer.model(Resources.id("species/wookiee")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_DEVARONIAN, Resources.id("textures/gui/character/icons.png"), 5);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_DEVARONIAN, SwgSpeciesRenderer.model(Resources.id("species/devaronian")), BaseClientContent::animateDevaronian);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_RODIAN, Resources.id("textures/gui/character/icons.png"), 11);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_RODIAN, SwgSpeciesRenderer.model(Resources.id("species/rodian")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_DUROS, Resources.id("textures/gui/character/icons.png"), 7);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_DUROS, SwgSpeciesRenderer.model(Resources.id("species/duros")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_GOTAL, Resources.id("textures/gui/character/icons.png"), 18);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_GOTAL, SwgSpeciesRenderer.model(Resources.id("species/gotal")), BaseClientContent::animateGotal);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_ONGREE, Resources.id("textures/gui/character/icons.png"), 19);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_ONGREE, SwgSpeciesRenderer.model(Resources.id("species/ongree")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_GRAN, Resources.id("textures/gui/character/icons.png"), 20);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_GRAN, SwgSpeciesRenderer.model(Resources.id("species/gran")), null);

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

		var yaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.headYaw) - MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
		var y = MathHelper.wrapDegrees(yaw) * MathHelper.RADIANS_PER_DEGREE;
		tailBaseL.roll = Math.max(0, y / 3f) + Math.min(0, y / 9f);
		tailBaseR.roll = Math.min(0, y / 3f) + Math.max(0, y / 9f);
	}

	public static void animateDevaronian(SwgSpecies species, AbstractClientPlayerEntity entity, PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerSpeciesModelRenderer renderer, float tickDelta)
	{
		var hornsVariable = species.getVariableReference("horns");
		if (hornsVariable == null)
			return;

		var horns = species.getVariable(hornsVariable);

		for (var i = 0; i < 6; i++)
		{
			var hornId = String.valueOf(i + 1);
			var childId = "horns" + hornId;
			if (!model.head.hasChild(childId))
				continue;

			model.head.getChild(childId).visible = horns.equals(hornId);
		}
	}

	public static void animateGotal(SwgSpecies species, AbstractClientPlayerEntity entity, PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerSpeciesModelRenderer renderer, float tickDelta)
	{
		var hornsVariable = species.getVariableReference("horns");
		var beardVariable = species.getVariableReference("beard");
		if (hornsVariable == null || beardVariable == null)
			return;

		var horns = species.getVariable(hornsVariable);
		var beard = species.getVariable(beardVariable);

		for (var i = 0; i < 6; i++)
		{
			var hornId = String.valueOf(i + 1);
			var childId = "horns" + hornId;
			if (!model.head.hasChild(childId))
				continue;

			model.head.getChild(childId).visible = horns.equals(hornId);
		}

		for (var i = 0; i < 7; i++)
		{
			var beardId = String.valueOf(i + 1);
			var childId = "beard" + beardId;
			if (!model.head.hasChild(childId))
				continue;

			model.head.getChild(childId).visible = beard.equals(beardId);
		}
	}
}
