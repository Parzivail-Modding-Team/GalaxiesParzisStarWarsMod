package com.parzivail.pswg.client.species;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SpeciesGender;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.client.render.player.PlayerSpeciesModelRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.function.Supplier;

public class SwgSpeciesRenderer
{
	public static final Identifier HUMANOID_BASE_MODEL_ID = Resources.id("species/humanoid_base");

	public static final HashMap<Identifier, SwgSpeciesModel> MODELS = new HashMap<>();

	//		register(SwgSpeciesRegistry.SPECIES_KAMINOAN, new ModelKaminoan<>(true, 0), new ModelKaminoan<>(false, 0));
	//		register(new SwgSpeciesModel(SwgSpeciesRegistry.SPECIES_WOOKIEE_M, new ModelWookiee<>(true, 0)));
	//		register(new SwgSpeciesModel(SwgSpeciesRegistry.SPECIES_WOOKIEE_F, new ModelWookiee<>(false, 0)));
	//		register(SwgSpeciesRegistry.SPECIES_BOTHAN, EMPTY_MODEL);

	public static Supplier<BipedEntityModel<LivingEntity>> getHumanModel()
	{
		return Client.NEM_MANAGER.getOverridingBipedModel(Resources.id("species/human"), HUMANOID_BASE_MODEL_ID, true);
	}

	public static Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> fullModel(Identifier id)
	{
		return Client.NEM_MANAGER.getPlayerModel(id, true);
	}

	public static Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> humanoidBase(Identifier id)
	{
		return Client.NEM_MANAGER.getOverridingPlayerModel(id, HUMANOID_BASE_MODEL_ID, true);
	}

	private static void register(SwgSpeciesModel model)
	{
		MODELS.put(model.identifier(), model);
	}

	private static void register(Identifier speciesSlug, SpeciesGender gender, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> model, PlayerSpeciesModelRenderer.Animator animator)
	{
		register(new SwgSpeciesModel(SwgSpecies.toModel(speciesSlug, gender), Suppliers.memoize(model::get), animator));
	}

	public static void register(Identifier speciesSlug, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> male, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> female, PlayerSpeciesModelRenderer.Animator animator)
	{
		register(speciesSlug, SpeciesGender.MALE, male, animator);
		register(speciesSlug, SpeciesGender.FEMALE, female, animator);
	}

	public static void register(Identifier speciesSlug, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> androgynousModel, PlayerSpeciesModelRenderer.Animator animator)
	{
		register(speciesSlug, SpeciesGender.MALE, androgynousModel, animator);
		register(speciesSlug, SpeciesGender.FEMALE, androgynousModel, animator);
	}

	public static Identifier getTexture(PlayerEntity player, SwgSpecies species)
	{
		var hashCode = species.longHashCode();
		return Client.stackedTextureProvider.getId(String.format("species/%016x", hashCode), () -> Client.TEX_TRANSPARENT, () -> species.getTextureStack(player));
	}

	public static void animateTwilek(AbstractClientPlayerEntity entity, PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerSpeciesModelRenderer renderer, float tickDelta)
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

		var y = MathHelper.wrapDegrees(entity.getYaw(tickDelta) - MathHelper.lerp(tickDelta, entity.prevBodyYaw, entity.bodyYaw)) * MathHelper.RADIANS_PER_DEGREE;
		tailBaseL.roll = Math.max(0, y / 3f);
		tailBaseR.roll = Math.min(0, y / 3f);
	}
}
